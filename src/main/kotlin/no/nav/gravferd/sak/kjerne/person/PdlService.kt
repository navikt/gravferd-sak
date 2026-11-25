package no.nav.gravferd.sak.kjerne.person

interface PdlService {
    fun hentPerson(personident: String): Person
}
