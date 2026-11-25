package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FagsakDboMapperTest {
    @Nested
    inner class TilDbo {
        @Test
        fun `overfører id og alle metadatafeltene`() {
            // Arrange
            val opprettetTidspunkt = LocalDateTime.of(2026, 1, 2, 3, 4, 5)
            val endretTidspunkt = LocalDateTime.of(2026, 2, 3, 4, 5, 6)
            val fagsak =
                Fagsak(
                    id = 42,
                    metadata =
                        Entitetsmetadata(
                            versjon = 7,
                            opprettetAv = "oppretter",
                            opprettetTidspunkt = opprettetTidspunkt,
                            endretAv = "endrer",
                            endretTidspunkt = endretTidspunkt,
                        ),
                )

            // Act
            val dbo = FagsakDboMapper.tilDbo(fagsak)

            // Assert
            assertThat(dbo.id).isEqualTo(42)
            assertThat(dbo.versjon).isEqualTo(7)
            assertThat(dbo.opprettetAv).isEqualTo("oppretter")
            assertThat(dbo.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
            assertThat(dbo.endretAv).isEqualTo("endrer")
            assertThat(dbo.endretTidspunkt).isEqualTo(endretTidspunkt)
        }
    }
}
