package no.nav.gravferd.sak.kjerne.fagsak

interface FagsakRepository {
    fun opprett(): Fagsak

    fun oppdater(fagsak: Fagsak): Fagsak

    fun hent(id: Long): Fagsak?
}
