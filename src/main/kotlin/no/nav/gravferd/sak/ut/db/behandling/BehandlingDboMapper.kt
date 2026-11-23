package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.ut.db.DboMapper

object BehandlingDboMapper : DboMapper<Behandling, BehandlingDbo>() {
    override fun lagDboUtenMetadata(behandling: Behandling): BehandlingDbo =
        BehandlingDbo(id = behandling.id, fagsakId = behandling.fagsakId)
}
