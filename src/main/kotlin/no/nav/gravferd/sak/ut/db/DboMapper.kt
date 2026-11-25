package no.nav.gravferd.sak.ut.db

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import no.nav.gravferd.sak.kjerne.felles.HarEntitetsmetadata

abstract class DboMapper<D : HarEntitetsmetadata, T : BaseEntitet> {
    fun tilDbo(domene: D): T = lagDboUtenMetadata(domene).medMetadata(domene.metadata)

    protected abstract fun lagDboUtenMetadata(domene: D): T

    // Viktig at versjon, opprettetAv og opprettetTidspunkt settes ved oppdatering av dbo.
    // Versjon trengs for å verifisere at det ikke er gjort endringer på entiteten etter at den ble hentet.
    // opprettetAv og opprettetTidspunkt vil ved oppdatering returnere null dersom de ikke sendes inn.
    private fun T.medMetadata(metadata: Entitetsmetadata): T =
        apply {
            versjon = metadata.versjon
            opprettetAv = metadata.opprettetAv
            opprettetTidspunkt = metadata.opprettetTidspunkt
        }
}
