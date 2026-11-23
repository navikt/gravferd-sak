package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.inn.rest.EntitetsmetadataDto
import no.nav.gravferd.sak.inn.rest.tilDto
import no.nav.gravferd.sak.kjerne.fagsak.Fagsak

data class FagsakDto(
    val id: Long,
    val metadata: EntitetsmetadataDto,
)

fun Fagsak.tilDto() =
    FagsakDto(
        id = id,
        metadata = metadata.tilDto(),
    )
