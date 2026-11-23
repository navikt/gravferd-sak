package no.nav.gravferd.sak.ut.db.behandling

import no.nav.gravferd.sak.AbstractIntegrasjonstest
import no.nav.gravferd.sak.config.sikkerhet.gyldigeClaims
import no.nav.gravferd.sak.config.sikkerhet.settSecurityHolderContext
import no.nav.gravferd.sak.fake.FakeClockProvider.Companion.STANDARDTIDSPUNKT
import no.nav.gravferd.sak.ut.db.fagsak.DefaultFagsakRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.security.core.context.SecurityContextHolder

class DefaultBehandlingRepositoryTest : AbstractIntegrasjonstest() {
    @Autowired
    private lateinit var fagsakRepository: DefaultFagsakRepository

    @Autowired
    private lateinit var behandlingRepository: DefaultBehandlingRepository

    @AfterEach
    fun ryddOpp() = SecurityContextHolder.clearContext()

    @Nested
    inner class Opprett {
        @Test
        fun `lagrer behandlingen med sporing fra systemet`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            val behandling = behandlingRepository.opprett(fagsak.id)

            // Act
            val hentet = behandlingRepository.hent(behandling.id)

            // Assert
            assertThat(hentet).isEqualTo(behandling)
            assertThat(hentet?.fagsakId).isEqualTo(fagsak.id)
            assertThat(hentet?.metadata?.opprettetAv).isEqualTo("VL")
            assertThat(hentet?.metadata?.opprettetTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
            assertThat(hentet?.metadata?.endretAv).isEqualTo("VL")
            assertThat(hentet?.metadata?.endretTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
        }

        @Test
        fun `lagrer innlogget saksbehandler som oppretter`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            settSecurityHolderContext()

            // Act
            val behandling = behandlingRepository.opprett(fagsak.id)

            // Assert
            val hentet = behandlingRepository.hent(behandling.id)
            assertThat(hentet?.metadata?.opprettetAv).isEqualTo(gyldigeClaims["NAVident"])
            assertThat(hentet?.metadata?.endretAv).isEqualTo(gyldigeClaims["NAVident"])
        }

        @Test
        fun `feiler når fagsaken ikke finnes`() {
            // Act
            val feil = catchThrowable { behandlingRepository.opprett(Long.MAX_VALUE) }

            // Assert
            assertThat(feil).isInstanceOf(DataIntegrityViolationException::class.java)
        }
    }

    @Nested
    inner class HentForFagsak {
        @Test
        fun `gir bare behandlingene til fagsaken`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            val annenFagsak = fagsakRepository.opprett()
            val første = behandlingRepository.opprett(fagsak.id)
            val andre = behandlingRepository.opprett(fagsak.id)
            behandlingRepository.opprett(annenFagsak.id)

            // Act
            val behandlinger = behandlingRepository.hentForFagsak(fagsak.id)

            // Assert
            assertThat(behandlinger).containsExactlyInAnyOrder(første, andre)
        }

        @Test
        fun `gir tom liste når fagsaken ikke har behandlinger`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()

            // Act
            val behandlinger = behandlingRepository.hentForFagsak(fagsak.id)

            // Assert
            assertThat(behandlinger).isEmpty()
        }
    }

    @Nested
    inner class Hent {
        @Test
        fun `gir null når behandlingen ikke finnes`() {
            // Act
            val hentet = behandlingRepository.hent(Long.MAX_VALUE)

            // Assert
            assertThat(hentet).isNull()
        }
    }

    @Nested
    inner class Oppdater {
        @Test
        fun `skal oppdatere endretAv, endretTidspunkt og versjon, men beholde opprettetAv og opprettetTidspunkt`() {
            // Arrange
            SecurityContextHolder.clearContext()
            val fagsak = fagsakRepository.opprett()
            val opprettet = behandlingRepository.opprett(fagsak.id)
            val nyttTidspunkt = STANDARDTIDSPUNKT.plusDays(1)
            fakeClockProvider.settTid(nyttTidspunkt)
            settSecurityHolderContext(claims = gyldigeClaims + ("NAVident" to "Z654321"))

            // Act
            val oppdatert = behandlingRepository.oppdater(opprettet)
            val hentet = behandlingRepository.hent(opprettet.id)

            // Assert
            assertThat(opprettet.metadata.opprettetAv).isEqualTo("VL")
            assertThat(opprettet.metadata.opprettetTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
            assertThat(oppdatert.id).isEqualTo(opprettet.id)
            assertThat(oppdatert.fagsakId).isEqualTo(opprettet.fagsakId)
            assertThat(oppdatert.metadata.versjon).isEqualTo(opprettet.metadata.versjon + 1)
            assertThat(oppdatert.metadata.opprettetAv).isEqualTo(opprettet.metadata.opprettetAv)
            assertThat(oppdatert.metadata.opprettetTidspunkt).isEqualTo(opprettet.metadata.opprettetTidspunkt)
            assertThat(oppdatert.metadata.endretAv).isEqualTo("Z654321")
            assertThat(oppdatert.metadata.endretTidspunkt).isEqualTo(nyttTidspunkt)
            assertThat(hentet).isEqualTo(oppdatert)
        }

        @Test
        fun `skal avvise oppdatering med utdatert versjon uten å endre lagret behandling`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            val opprinnelig = behandlingRepository.opprett(fagsak.id)
            val gjeldende = behandlingRepository.oppdater(opprinnelig)

            // Act
            val feil = catchThrowable { behandlingRepository.oppdater(opprinnelig) }
            val hentet = behandlingRepository.hent(opprinnelig.id)

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
            assertThat(hentet).isEqualTo(gjeldende)
        }

        @Test
        fun `skal avvise oppdatering av ukjent id`() {
            // Arrange
            val fagsak = fagsakRepository.opprett()
            val opprettet = behandlingRepository.opprett(fagsak.id)

            // Act
            val feil =
                catchThrowable {
                    behandlingRepository.oppdater(opprettet.copy(id = Long.MAX_VALUE))
                }

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
        }

        @Test
        fun `skal ikke overskrive opprettetAv og opprettetTidspunkt ved oppdatering selv om de er forsøkt endret`() {
            // Arrange
            SecurityContextHolder.clearContext()
            val fagsak = fagsakRepository.opprett()
            val opprettet = behandlingRepository.opprett(fagsak.id)
            val endretOpprettingsinformasjon =
                opprettet.copy(
                    metadata =
                        opprettet.metadata.copy(
                            opprettetAv = "annen bruker",
                            opprettetTidspunkt = STANDARDTIDSPUNKT.plusDays(2),
                        ),
                )

            // Act
            behandlingRepository.oppdater(endretOpprettingsinformasjon)
            val hentet = behandlingRepository.hent(opprettet.id)

            // Assert
            assertThat(hentet?.metadata?.opprettetAv).isEqualTo(opprettet.metadata.opprettetAv)
            assertThat(hentet?.metadata?.opprettetTidspunkt).isEqualTo(opprettet.metadata.opprettetTidspunkt)
        }
    }
}
