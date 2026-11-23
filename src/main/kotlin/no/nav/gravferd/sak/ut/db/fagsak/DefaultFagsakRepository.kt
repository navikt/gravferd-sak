package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.fagsak.FagsakRepository
import no.nav.gravferd.sak.ut.db.DboRepository
import no.nav.gravferd.sak.ut.db.tilMetadata
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.stereotype.Repository

@Repository
class DefaultFagsakRepository(
    jdbcAggregateOperations: JdbcAggregateOperations,
) : DboRepository<FagsakDbo, Long>(jdbcAggregateOperations, FagsakDbo::class.java),
    FagsakRepository {
    override fun opprett(): Fagsak = opprettDbo(FagsakDbo()).tilDomene()

    override fun oppdater(fagsak: Fagsak): Fagsak = oppdaterDbo(FagsakDboMapper.tilDbo(fagsak)).tilDomene()

    override fun hent(id: Long): Fagsak? = hentDbo(id)?.tilDomene()

    private fun FagsakDbo.tilDomene() =
        Fagsak(
            id = checkNotNull(id) { "Fagsak mangler id etter lagring" },
            metadata = this.tilMetadata(),
        )
}
