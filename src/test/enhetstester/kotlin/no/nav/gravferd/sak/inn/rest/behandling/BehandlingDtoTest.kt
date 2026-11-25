package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.inn.rest.EntitetsmetadataDto
import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BehandlingDtoTest {
    @Nested
    inner class FraDomene {
        @Test
        fun `overfører id, fagsakId og alle metadatafeltene`() {
            // Arrange
            val opprettetTidspunkt = LocalDateTime.of(2026, 11, 23, 10, 15)
            val endretTidspunkt = LocalDateTime.of(2026, 11, 23, 11, 30)
            val behandling =
                Behandling(
                    id = 52L,
                    fagsakId = 42L,
                    metadata =
                        Entitetsmetadata(
                            versjon = 7L,
                            opprettetAv = "oppretter",
                            opprettetTidspunkt = opprettetTidspunkt,
                            endretAv = "endrer",
                            endretTidspunkt = endretTidspunkt,
                        ),
                )
            val forventet =
                BehandlingDto(
                    id = 52L,
                    fagsakId = 42L,
                    metadata =
                        EntitetsmetadataDto(
                            versjon = 7L,
                            opprettetAv = "oppretter",
                            opprettetTidspunkt = opprettetTidspunkt,
                            endretAv = "endrer",
                            endretTidspunkt = endretTidspunkt,
                        ),
                )

            // Act
            val dto = BehandlingDto.fraDomene(behandling)

            // Assert
            assertThat(dto).isEqualTo(forventet)
        }
    }
}
