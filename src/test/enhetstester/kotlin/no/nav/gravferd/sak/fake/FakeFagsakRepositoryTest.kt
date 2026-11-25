package no.nav.gravferd.sak.fake

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.OptimisticLockingFailureException

class FakeFagsakRepositoryTest {
    @Nested
    inner class Oppdater {
        @Test
        fun `lagrer fagsaken og øker versjonen`() {
            // Arrange
            val repository = FakeFagsakRepository()
            val opprettet = repository.opprett()

            // Act
            val returnert = repository.oppdater(opprettet)

            // Assert
            assertThat(returnert.id).isEqualTo(opprettet.id)
            assertThat(returnert.metadata.versjon).isEqualTo(opprettet.metadata.versjon + 1)
            assertThat(repository.hent(opprettet.id)).isEqualTo(returnert)
        }

        @Test
        fun `ukjent id feiler uten å opprette fagsak`() {
            // Arrange
            val repository = FakeFagsakRepository()
            val ukjent = repository.opprett().copy(id = Long.MAX_VALUE)

            // Act
            val feil = catchThrowable { repository.oppdater(ukjent) }

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
            assertThat(repository.hent(ukjent.id)).isNull()
        }

        @Test
        fun `gammel versjon avvises uten å endre lagret fagsak`() {
            // Arrange
            val repository = FakeFagsakRepository()
            val opprettet = repository.opprett()
            val oppdatert = repository.oppdater(opprettet)

            // Act
            val feil = catchThrowable { repository.oppdater(opprettet) }

            // Assert
            assertThat(feil).isInstanceOf(OptimisticLockingFailureException::class.java)
            assertThat(repository.hent(opprettet.id)).isEqualTo(oppdatert)
        }
    }
}
