package no.nav.gravferd.sak.ut.db

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import no.nav.gravferd.sak.ut.db.fagsak.FagsakDbo
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EntitetsmetadataMapperTest {
    @Test
    fun `tilMetadata kopierer alle metadatafeltene`() {
        val opprettetTidspunkt = LocalDateTime.of(2026, 5, 6, 7, 8, 9)
        val endretTidspunkt = LocalDateTime.of(2026, 6, 7, 8, 9, 10)
        val dbo =
            FagsakDbo(id = 1).apply {
                versjon = 3
                opprettetAv = "oppretter"
                this.opprettetTidspunkt = opprettetTidspunkt
                endretAv = "endrer"
                this.endretTidspunkt = endretTidspunkt
            }

        val metadata = dbo.tilMetadata()

        assertThat(metadata.versjon).isEqualTo(3)
        assertThat(metadata.opprettetAv).isEqualTo("oppretter")
        assertThat(metadata.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(metadata.endretAv).isEqualTo("endrer")
        assertThat(metadata.endretTidspunkt).isEqualTo(endretTidspunkt)
    }

    @Test
    fun `tilMetadata beholder null i endretAv og endretTidspunkt`() {
        val opprettetTidspunkt = LocalDateTime.of(2026, 5, 6, 7, 8, 9)
        val dbo =
            FagsakDbo(id = 1).apply {
                versjon = 3
                opprettetAv = "oppretter"
                this.opprettetTidspunkt = opprettetTidspunkt
            }

        val metadata = dbo.tilMetadata()

        assertThat(metadata.versjon).isEqualTo(3)
        assertThat(metadata.opprettetAv).isEqualTo("oppretter")
        assertThat(metadata.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(metadata.endretAv).isNull()
        assertThat(metadata.endretTidspunkt).isNull()
    }

    @Test
    fun `tilMetadata avviser manglende opprettetAv`() {
        val dbo =
            FagsakDbo(id = 1).apply {
                opprettetTidspunkt = LocalDateTime.of(2026, 5, 6, 7, 8, 9)
            }

        val feil = catchThrowable { dbo.tilMetadata() }

        assertThat(feil)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Entiteten mangler opprettetAv")
    }

    @Test
    fun `tilMetadata avviser manglende opprettetTidspunkt`() {
        val dbo = FagsakDbo(id = 1).apply { opprettetAv = "oppretter" }

        val feil = catchThrowable { dbo.tilMetadata() }

        assertThat(feil)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Entiteten mangler opprettetTidspunkt")
    }

    @Test
    fun `medMetadata overfører versjon og opprettingsfelter uten å endre endringsmetadata`() {
        val eksisterendeEndretTidspunkt = LocalDateTime.of(2026, 7, 8, 9, 10, 11)
        val opprettetTidspunkt = LocalDateTime.of(2026, 1, 2, 3, 4, 5)
        val metadataEndretTidspunkt = LocalDateTime.of(2026, 2, 3, 4, 5, 6)
        val dbo =
            FagsakDbo(id = 1).apply {
                versjon = 1
                opprettetAv = "gammel oppretter"
                this.opprettetTidspunkt = LocalDateTime.of(2025, 1, 2, 3, 4, 5)
                endretAv = "eksisterende endrer"
                endretTidspunkt = eksisterendeEndretTidspunkt
            }
        val metadata =
            Entitetsmetadata(
                versjon = 8,
                opprettetAv = "ny oppretter",
                opprettetTidspunkt = opprettetTidspunkt,
                endretAv = "metadata endrer",
                endretTidspunkt = metadataEndretTidspunkt,
            )

        val resultat = dbo.medMetadata(metadata)

        assertThat(resultat).isSameAs(dbo)
        assertThat(dbo.versjon).isEqualTo(8)
        assertThat(dbo.opprettetAv).isEqualTo("ny oppretter")
        assertThat(dbo.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(dbo.endretAv).isEqualTo("eksisterende endrer")
        assertThat(dbo.endretTidspunkt).isEqualTo(eksisterendeEndretTidspunkt)
    }
}
