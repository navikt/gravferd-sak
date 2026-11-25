package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.inn.rest.EntitetsmetadataDto
import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FagsakDtoTest {
    @Nested
    inner class FraDomene {
        @Test
        fun `overfører id og alle metadatafeltene`() {
            // Arrange
            val opprettetTidspunkt = LocalDateTime.of(2026, 11, 23, 10, 15)
            val endretTidspunkt = LocalDateTime.of(2026, 11, 23, 11, 30)
            val fagsak =
                Fagsak(
                    id = 42L,
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
                FagsakDto(
                    id = 42L,
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
            val dto = FagsakDto.fraDomene(fagsak)

            // Assert
            assertThat(dto).isEqualTo(forventet)
        }
    }
}
