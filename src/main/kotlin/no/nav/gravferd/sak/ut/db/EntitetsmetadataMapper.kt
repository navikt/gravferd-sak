package no.nav.gravferd.sak.ut.db

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata

fun BaseEntitet.tilMetadata(): Entitetsmetadata =
    Entitetsmetadata(
        versjon = versjon,
        opprettetAv =
            checkNotNull(opprettetAv) {
                "Entiteten mangler opprettetAv"
            },
        opprettetTidspunkt =
            checkNotNull(opprettetTidspunkt) {
                "Entiteten mangler opprettetTidspunkt"
            },
        endretAv = endretAv,
        endretTidspunkt = endretTidspunkt,
    )

// Viktig at versjon, opprettetAv og opprettetTidspunkt settes ved oppdatering av dbo.
// Versjon trengs for å verifisere at det ikke er gjort endringer på entiteten etter at den ble hentet.
// opprettetAv og opprettetTidspunkt vil ved oppdatering returnere null dersom de ikke sendes inn.
fun <T : BaseEntitet> T.medMetadata(metadata: Entitetsmetadata): T =
    apply {
        versjon = metadata.versjon
        opprettetAv = metadata.opprettetAv
        opprettetTidspunkt = metadata.opprettetTidspunkt
    }
