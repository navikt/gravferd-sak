package no.nav.gravferd.sak.kjerne.felles

import java.time.LocalDateTime

data class Entitetsmetadata(
    val versjon: Long,
    val opprettetAv: String,
    val opprettetTidspunkt: LocalDateTime,
    val endretAv: String?,
    val endretTidspunkt: LocalDateTime?,
)
