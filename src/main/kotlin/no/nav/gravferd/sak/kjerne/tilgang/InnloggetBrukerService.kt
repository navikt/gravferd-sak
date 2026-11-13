package no.nav.gravferd.sak.kjerne.tilgang

interface InnloggetBrukerService {
    fun hentRoller(): Set<Rolle>
}
