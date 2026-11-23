package no.nav.gravferd.sak.ut.db.fagsak

import no.nav.gravferd.sak.AbstractIntegrasjonstest
import no.nav.gravferd.sak.config.sikkerhet.gyldigeClaims
import no.nav.gravferd.sak.config.sikkerhet.settSecurityHolderContext
import no.nav.gravferd.sak.fake.FakeClockProvider.Companion.STANDARDTIDSPUNKT
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.security.core.context.SecurityContextHolder

class DefaultFagsakRepositoryTest : AbstractIntegrasjonstest() {
    @Autowired
    private lateinit var fagsakRepository: DefaultFagsakRepository

    @AfterEach
    fun ryddOpp() = SecurityContextHolder.clearContext()

    @Test
    fun `opprettet fagsak kan hentes med sporing`() {
        // Arrange
        val fagsak = fagsakRepository.opprett()

        // Act
        val hentet = fagsakRepository.hent(fagsak.id)

        // Assert
        assertThat(hentet).isEqualTo(fagsak)
        assertThat(hentet?.metadata?.opprettetAv).isEqualTo("VL")
        assertThat(hentet?.metadata?.opprettetTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
        assertThat(hentet?.metadata?.endretAv).isEqualTo("VL")
        assertThat(hentet?.metadata?.endretTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
    }

    @Test
    fun `hver fagsak får egen id`() {
        // Act
        val første = fagsakRepository.opprett()
        val andre = fagsakRepository.opprett()

        // Assert
        assertThat(første.id).isNotEqualTo(andre.id)
    }

    @Test
    fun `hent gir null når fagsaken ikke finnes`() {
        // Act
        val hentet = fagsakRepository.hent(Long.MAX_VALUE)

        // Assert
        assertThat(hentet).isNull()
    }

    @Nested
    inner class Oppdater {
        @Test
        fun `skal oppdatere endretAv, endretTidspunkt og versjon, men beholde opprettetAv og opprettetTidspunkt`() {
            // Arrange
            SecurityContextHolder.clearContext()
            val opprettet = fagsakRepository.opprett()
            val nyttTidspunkt = STANDARDTIDSPUNKT.plusDays(1)
            fakeClockProvider.settTid(nyttTidspunkt)
            settSecurityHolderContext(claims = gyldigeClaims + ("NAVident" to "Z654321"))

            // Act
            val oppdatert = fagsakRepository.oppdater(opprettet)
            val hentet = fagsakRepository.hent(opprettet.id)

            // Assert
            assertThat(opprettet.metadata.opprettetAv).isEqualTo("VL")
            assertThat(opprettet.metadata.opprettetTidspunkt).isEqualTo(STANDARDTIDSPUNKT)
            assertThat(oppdatert.id).isEqualTo(opprettet.id)
            assertThat(oppdatert.metadata.versjon).isEqualTo(opprettet.metadata.versjon + 1)
            assertThat(oppdatert.metadata.opprettetAv).isEqualTo(opprettet.metadata.opprettetAv)
            assertThat(oppdatert.metadata.opprettetTidspunkt).isEqualTo(opprettet.metadata.opprettetTidspunkt)
            assertThat(oppdatert.metadata.endretAv).isEqualTo("Z654321")
            assertThat(oppdatert.metadata.endretTidspunkt).isEqualTo(nyttTidspunkt)
            assertThat(hentet).isEqualTo(oppdatert)
        }

        @Test
        fun `avviser oppdatering med utdatert versjon uten å endre lagret fagsak`() {
            // Arrange
            val opprinnelig = fagsakRepository.opprett()
            val gjeldende = fagsakRepository.oppdater(opprinnelig)

            // Act
            val feil = catchThrowable { fagsakRepository.oppdater(opprinnelig) }
            val hentet = fagsakRepository.hent(opprinnelig.id)

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
            assertThat(hentet).isEqualTo(gjeldende)
        }

        @Test
        fun `avviser oppdatering av ukjent id`() {
            // Arrange
            val opprettet = fagsakRepository.opprett()

            // Act
            val feil = catchThrowable { fagsakRepository.oppdater(opprettet.copy(id = Long.MAX_VALUE)) }

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
        }

        @Test
        fun `beholder opprettingsinformasjon i databasen når den endres i oppdateringen`() {
            // Arrange
            SecurityContextHolder.clearContext()
            val opprettet = fagsakRepository.opprett()
            val endretOpprettingsinformasjon =
                opprettet.copy(
                    metadata =
                        opprettet.metadata.copy(
                            opprettetAv = "annen bruker",
                            opprettetTidspunkt = STANDARDTIDSPUNKT.plusDays(2),
                        ),
                )

            // Act
            fagsakRepository.oppdater(endretOpprettingsinformasjon)
            val hentet = fagsakRepository.hent(opprettet.id)

            // Assert
            assertThat(hentet?.metadata?.opprettetAv).isEqualTo(opprettet.metadata.opprettetAv)
            assertThat(hentet?.metadata?.opprettetTidspunkt).isEqualTo(opprettet.metadata.opprettetTidspunkt)
        }
    }
}
