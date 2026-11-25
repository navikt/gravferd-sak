package no.nav.gravferd.sak.ut.rest.pdl

import no.nav.familie.kontrakter.felles.jsonMapper
import no.nav.familie.pdl.PdlException
import no.nav.familie.pdl.PdlHybridKlient
import no.nav.gravferd.sak.kjerne.feil.Feil
import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetFeil
import no.nav.gravferd.sak.kjerne.person.PdlService
import no.nav.gravferd.sak.kjerne.person.Person
import no.nav.gravferd.sak.ut.rest.pdl.dto.HENT_PERSONOPPLYSNINGER
import no.nav.gravferd.sak.ut.rest.pdl.dto.HentPersonopplysninger.Result
import no.nav.gravferd.sak.ut.rest.pdl.dto.HentPersonopplysninger.Variables
import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference
import java.time.LocalDate
import no.nav.gravferd.sak.ut.rest.pdl.dto.hentpersonopplysninger.Person as PdlPerson

@Component
class DefaultPdlService(
    private val pdlHybridKlient: PdlHybridKlient,
) : PdlService {
    override fun hentPerson(personident: String): Person {
        val pdlPerson =
            try {
                pdlHybridKlient
                    .hent<Result>(HENT_PERSONOPPLYSNINGER, Variables(ident = personident).tilMap())
                    .person
            } catch (feil: PdlException) {
                if (feil.errors.any { it.extensions?.code == "not_found" }) {
                    throw IkkeFunnetFeil("Fant ikke person i PDL")
                }
                throw feil
            } ?: throw Feil("Svar fra PDL mangler person")

        return tilPerson(pdlPerson)
    }
}

private fun Any.tilMap(): Map<String, Any?> = jsonMapper.convertValue(this, object : TypeReference<Map<String, Any?>>() {})

private fun tilPerson(pdlPerson: PdlPerson): Person {
    val navn = pdlPerson.navn.firstOrNull() ?: throw Feil("Person i PDL mangler navn")
    val formatertNavn = listOfNotNull(navn.fornavn, navn.mellomnavn, navn.etternavn).joinToString(" ")
    val fødselsdato =
        pdlPerson.foedselsdato
            .firstOrNull { it.foedselsdato != null }
            ?.foedselsdato
            ?.let(LocalDate::parse)
    val dødsdato =
        pdlPerson.doedsfall
            .firstOrNull { it.doedsdato != null }
            ?.doedsdato
            ?.let(LocalDate::parse)

    return Person(
        navn = formatertNavn,
        fødselsdato = fødselsdato,
        dødsdato = dødsdato,
    )
}
