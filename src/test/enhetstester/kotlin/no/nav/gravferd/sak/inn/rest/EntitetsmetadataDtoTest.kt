package no.nav.gravferd.sak.inn.rest

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EntitetsmetadataDtoTest {
    @Nested
    inner class FraDomene {
        @Test
        fun `overfører alle metadatafeltene`() {
            // Arrange
            val opprettetTidspunkt = LocalDateTime.of(2026, 11, 23, 10, 15)
            val endretTidspunkt = LocalDateTime.of(2026, 11, 23, 11, 30)
            val metadata =
                Entitetsmetadata(
                    versjon = 7L,
                    opprettetAv = "oppretter",
                    opprettetTidspunkt = opprettetTidspunkt,
                    endretAv = "endrer",
                    endretTidspunkt = endretTidspunkt,
                )

            // Act
            val dto = EntitetsmetadataDto.fraDomene(metadata)

            // Assert
            assertThat(dto.versjon).isEqualTo(7L)
            assertThat(dto.opprettetAv).isEqualTo("oppretter")
            assertThat(dto.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
            assertThat(dto.endretAv).isEqualTo("endrer")
            assertThat(dto.endretTidspunkt).isEqualTo(endretTidspunkt)
        }

        @Test
        fun `bevarer null i endringsfeltene`() {
            // Arrange
            val opprettetTidspunkt = LocalDateTime.of(2026, 11, 23, 10, 15)
            val metadata =
                Entitetsmetadata(
                    versjon = 1L,
                    opprettetAv = "oppretter",
                    opprettetTidspunkt = opprettetTidspunkt,
                    endretAv = null,
                    endretTidspunkt = null,
                )

            // Act
            val dto = EntitetsmetadataDto.fraDomene(metadata)

            // Assert
            assertThat(dto.endretAv).isNull()
            assertThat(dto.endretTidspunkt).isNull()
        }
    }
}
