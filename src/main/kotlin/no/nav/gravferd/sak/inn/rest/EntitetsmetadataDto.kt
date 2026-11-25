package no.nav.gravferd.sak.inn.rest

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import java.time.LocalDateTime

data class EntitetsmetadataDto(
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
) {
    companion object {
        fun fraDomene(entitetsmetadata: Entitetsmetadata): EntitetsmetadataDto =
            EntitetsmetadataDto(
                versjon = entitetsmetadata.versjon,
                opprettetAv = entitetsmetadata.opprettetAv,
                opprettetTidspunkt = entitetsmetadata.opprettetTidspunkt,
                endretAv = entitetsmetadata.endretAv,
                endretTidspunkt = entitetsmetadata.endretTidspunkt,
            )
    }
}
