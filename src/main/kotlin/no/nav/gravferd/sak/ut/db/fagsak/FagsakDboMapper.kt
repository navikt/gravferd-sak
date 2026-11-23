package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.ut.db.DboMapper

object FagsakDboMapper : DboMapper<Fagsak, FagsakDbo>() {
    override fun lagDboUtenMetadata(fagsak: Fagsak): FagsakDbo = FagsakDbo(id = fagsak.id)
}
