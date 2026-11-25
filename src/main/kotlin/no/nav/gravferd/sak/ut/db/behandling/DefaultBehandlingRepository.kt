package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.kjerne.behandling.BehandlingRepository
import no.nav.gravferd.sak.ut.db.DboRepository
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.stereotype.Repository

@Repository
class DefaultBehandlingRepository(
    jdbcAggregateOperations: JdbcAggregateOperations,
) : DboRepository<BehandlingDbo, Long>(jdbcAggregateOperations, BehandlingDbo::class.java),
    BehandlingRepository {
    override fun opprett(fagsakId: Long): Behandling = opprettDbo(BehandlingDbo(fagsakId = fagsakId)).tilDomene()

    override fun oppdater(behandling: Behandling): Behandling = oppdaterDbo(BehandlingDboMapper.tilDbo(behandling)).tilDomene()

    override fun hent(id: Long): Behandling? = hentDbo(id)?.tilDomene()

    override fun hentForFagsak(fagsakId: Long): List<Behandling> =
        finnAlleDbo(Query.query(Criteria.where("fagsakId") isEqual fagsakId))
            .map { it.tilDomene() }

    private fun BehandlingDbo.tilDomene() =
        Behandling(
            id = checkNotNull(id) { "Behandling mangler id etter lagring" },
            fagsakId = fagsakId,
            metadata = this.tilMetadata(),
        )
}
