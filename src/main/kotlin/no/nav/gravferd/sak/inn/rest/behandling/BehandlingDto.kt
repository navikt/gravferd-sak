package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.inn.rest.EntitetsmetadataDto
import no.nav.gravferd.sak.kjerne.behandling.Behandling

data class BehandlingDto(
    val id: Long,
    val fagsakId: Long,
    val metadata: EntitetsmetadataDto,
) {
    companion object {
        fun fraDomene(behandling: Behandling): BehandlingDto =
            BehandlingDto(
                id = behandling.id,
                fagsakId = behandling.fagsakId,
                metadata = EntitetsmetadataDto.fraDomene(behandling.metadata),
            )
    }
}
