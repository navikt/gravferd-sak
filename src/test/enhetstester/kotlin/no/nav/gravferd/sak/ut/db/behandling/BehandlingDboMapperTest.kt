package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BehandlingDboMapperTest {
    @Test
    fun `tilDbo overfører id, fagsakId og opprettingsmetadata, men utelater endringsmetadata`() {
        val opprettetTidspunkt = LocalDateTime.of(2026, 3, 4, 5, 6, 7)
        val endretTidspunkt = LocalDateTime.of(2026, 4, 5, 6, 7, 8)
        val behandling =
            Behandling(
                id = 52,
                fagsakId = 42,
                metadata =
                    Entitetsmetadata(
                        versjon = 9,
                        opprettetAv = "oppretter",
                        opprettetTidspunkt = opprettetTidspunkt,
                        endretAv = "endrer",
                        endretTidspunkt = endretTidspunkt,
                    ),
            )

        val dbo = BehandlingDboMapper.tilDbo(behandling)

        assertThat(dbo.id).isEqualTo(52)
        assertThat(dbo.fagsakId).isEqualTo(42)
        assertThat(dbo.versjon).isEqualTo(9)
        assertThat(dbo.opprettetAv).isEqualTo("oppretter")
        assertThat(dbo.opprettetTidspunkt).isEqualTo(opprettetTidspunkt)
        assertThat(dbo.endretAv).isNull()
        assertThat(dbo.endretTidspunkt).isNull()
    }
}
