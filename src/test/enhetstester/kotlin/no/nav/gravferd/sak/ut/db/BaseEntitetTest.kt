package no.nav.gravferd.sak.ut.db

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BaseEntitetTest {
    @Test
    fun `tilMetadata overfører alle metadatafeltene`() {
        // Arrange
        val opprettetTidspunkt = LocalDateTime.of(2026, 5, 6, 7, 8, 9)
        val endretTidspunkt = LocalDateTime.of(2026, 6, 7, 8, 9, 10)
        val entitet = object : BaseEntitet() {}
        entitet.apply {
            versjon = 3
            opprettetAv = "oppretter"
            this.opprettetTidspunkt = opprettetTidspunkt
            endretAv = "endrer"
            this.endretTidspunkt = endretTidspunkt
        }

        // Act
        val metadata = entitet.tilMetadata()

        // Assert
        assertThat(metadata.versjon).isEqualTo(3)
        assertThat(metadata.opprettetAv).isEqualTo("oppretter")
        assertThat(metadata.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(metadata.endretAv).isEqualTo("endrer")
        assertThat(metadata.endretTidspunkt).isEqualTo(endretTidspunkt)
    }

    @Test
    fun `tilMetadata bevarer null i endretAv og endretTidspunkt`() {
        // Arrange
        val opprettetTidspunkt = LocalDateTime.of(2026, 5, 6, 7, 8, 9)
        val entitet = object : BaseEntitet() {}
        entitet.apply {
            versjon = 3
            opprettetAv = "oppretter"
            this.opprettetTidspunkt = opprettetTidspunkt
        }

        // Act
        val metadata = entitet.tilMetadata()

        // Assert
        assertThat(metadata.versjon).isEqualTo(3)
        assertThat(metadata.opprettetAv).isEqualTo("oppretter")
        assertThat(metadata.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(metadata.endretAv).isNull()
        assertThat(metadata.endretTidspunkt).isNull()
    }

    @Test
    fun `tilMetadata avviser manglende opprettetAv`() {
        // Arrange
        val entitet = object : BaseEntitet() {}
        entitet.opprettetTidspunkt = LocalDateTime.of(2026, 5, 6, 7, 8, 9)

        // Act
        val feil = catchThrowable { entitet.tilMetadata() }

        // Assert
        assertThat(feil)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Entiteten mangler opprettetAv")
    }

    @Test
    fun `tilMetadata avviser manglende opprettetTidspunkt`() {
        // Arrange
        val entitet = object : BaseEntitet() {}
        entitet.opprettetAv = "oppretter"

        // Act
        val feil = catchThrowable { entitet.tilMetadata() }

        // Assert
        assertThat(feil)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Entiteten mangler opprettetTidspunkt")
    }
}
