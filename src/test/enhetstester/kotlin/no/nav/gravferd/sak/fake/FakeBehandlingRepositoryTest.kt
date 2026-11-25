package no.nav.gravferd.sak.fake

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.OptimisticLockingFailureException

class FakeBehandlingRepositoryTest {
    @Nested
    inner class Oppdater {
        @Test
        fun `lagrer behandlingen og øker versjonen`() {
            // Arrange
            val repository = FakeBehandlingRepository()
            val opprettet = repository.opprett(fagsakId = 123)
            val endret = opprettet.copy(fagsakId = 456)

            // Act
            val returnert = repository.oppdater(endret)

            // Assert
            assertThat(returnert.fagsakId).isEqualTo(endret.fagsakId)
            assertThat(returnert.id).isEqualTo(opprettet.id)
            assertThat(returnert.metadata.versjon).isEqualTo(opprettet.metadata.versjon + 1)
            assertThat(repository.hent(opprettet.id)).isEqualTo(returnert)
        }

        @Test
        fun `ukjent id feiler uten å opprette behandling`() {
            // Arrange
            val repository = FakeBehandlingRepository()
            val ukjent = repository.opprett(fagsakId = 123).copy(id = Long.MAX_VALUE)

            // Act
            val feil = catchThrowable { repository.oppdater(ukjent) }

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
            assertThat(repository.hent(ukjent.id)).isNull()
        }

        @Test
        fun `gammel versjon avvises uten å endre lagret behandling`() {
            // Arrange
            val repository = FakeBehandlingRepository()
            val opprettet = repository.opprett(fagsakId = 123)
            val oppdatert = repository.oppdater(opprettet)

            // Act
            val feil = catchThrowable { repository.oppdater(opprettet) }

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
            assertThat(repository.hent(opprettet.id)).isEqualTo(oppdatert)
        }
    }
}
