package no.nav.gravferd.sak.inn.rest

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import java.time.LocalDateTime

data class EntitetsmetadataDto(
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
)

fun Entitetsmetadata.tilDto() =
    EntitetsmetadataDto(
        versjon = versjon,
        opprettetAv = opprettetAv,
        opprettetTidspunkt = opprettetTidspunkt,
        endretAv = endretAv,
        endretTidspunkt = endretTidspunkt,
    )
