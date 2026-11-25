package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetFeil
import no.nav.gravferd.sak.kjerne.person.PdlService
import no.nav.gravferd.sak.kjerne.person.Person

class FakePdlService : PdlService {
    private val personer = ThreadLocal.withInitial { mutableMapOf<String, Person>() }

    override fun hentPerson(personident: String): Person = personer.get()[personident] ?: throw IkkeFunnetFeil("Fant ikke person i PDL")

    fun leggTilPerson(
        personident: String,
        person: Person,
    ) {
        personer.get()[personident] = person
    }

    fun nullstill() = personer.remove()
}
