package no.nav.gravferd.sak.inn.kafka.identhendelse

import no.nav.gravferd.sak.kjerne.personident.Identhendelse
import no.nav.person.pdl.aktor.v2.Aktor
import org.springframework.stereotype.Component

@Component
class IdenthendelseMapper {
    fun tilIdenthendelse(aktor: Aktor?): Identhendelse = TODO("Map identifikatorene uten å miste historikk; avklar null og tvetydige data")
}
