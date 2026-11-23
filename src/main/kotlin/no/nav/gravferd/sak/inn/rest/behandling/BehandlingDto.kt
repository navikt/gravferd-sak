package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.inn.rest.EntitetsmetadataDto
import no.nav.gravferd.sak.inn.rest.tilDto
import no.nav.gravferd.sak.kjerne.behandling.Behandling

data class BehandlingDto(
    val id: Long,
    val fagsakId: Long,
    val metadata: EntitetsmetadataDto,
)

fun Behandling.tilDto() =
    BehandlingDto(
        id = id,
        fagsakId = fagsakId,
        metadata = metadata.tilDto(),
    )
