package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FagsakDboMapperTest {
    @Test
    fun `tilDbo overfører id og opprettingsmetadata, men utelater endringsmetadata`() {
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

        val dbo = FagsakDboMapper.tilDbo(fagsak)

        assertThat(dbo.id).isEqualTo(42)
        assertThat(dbo.versjon).isEqualTo(7)
        assertThat(dbo.opprettetAv).isEqualTo("oppretter")
        assertThat(dbo.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(dbo.endretAv).isNull()
        assertThat(dbo.endretTidspunkt).isNull()
    }
}
