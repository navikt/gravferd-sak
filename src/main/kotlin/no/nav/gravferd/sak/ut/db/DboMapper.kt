package no.nav.gravferd.sak.ut.db

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import no.nav.gravferd.sak.kjerne.felles.HarEntitetsmetadata

abstract class DboMapper<D : HarEntitetsmetadata, T : BaseEntitet> {
    fun tilDbo(domene: D): T = lagDboUtenMetadata(domene).medMetadata(domene.metadata)

    protected abstract fun lagDboUtenMetadata(domene: D): T

    // Alle metadatafeltene kopieres med forventet versjon uendret.
    // Auditing setter ny endringsbruker og nytt endringstidspunkt ved lagring.
    private fun T.medMetadata(metadata: Entitetsmetadata): T =
        apply {
            versjon = metadata.versjon
            opprettetAv = metadata.opprettetAv
            opprettetTidspunkt = metadata.opprettetTidspunkt
            endretAv = metadata.endretAv
            endretTidspunkt = metadata.endretTidspunkt
        }
}
