package no.nav.gravferd.sak.config.sikkerhet

import no.nav.familie.felles.klient.KlientTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.springframework.security.core.context.SecurityContextHolder

@TestInstance(PER_METHOD)
class KlientTokenConfigTest {
    private val klientTokenProvider: KlientTokenProvider = KlientTokenConfig().klientTokenProvider()

    @AfterEach
    fun ryddOpp() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `gir token fra innlogget bruker`() {
        // Arrange
        settSecurityHolderContext()

        // Act
        val token = klientTokenProvider.hentToken()

        // Assert
        assertThat(token).isEqualTo("test-token")
    }

    @Test
    fun `gir null uten innlogget bruker`() {
        // Arrange
        SecurityContextHolder.clearContext()

        // Act
        val token = klientTokenProvider.hentToken()

        // Assert
        assertThat(token).isNull()
    }
}
