package no.nav.gravferd.sak.kjerne.tilgang

import no.nav.gravferd.sak.fake.FakeInnloggetBrukerService
import no.nav.gravferd.sak.kjerne.feil.ManglerTilgangFeil
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.BESLUTTER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.FORVALTER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.SAKSBEHANDLER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.VEILEDER
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class TilgangServiceTest {
    private val innloggetBrukerService = FakeInnloggetBrukerService()
    private val tilgangService = TilgangService(innloggetBrukerService)

    @AfterEach
    fun nullstill() = innloggetBrukerService.nullstill()

    @ParameterizedTest(name = "{0} får tilgang når minimum er {1}")
    @MethodSource("girTilgang")
    fun `gir tilgang`(
        roller: Set<Rolle>,
        minimumRolle: Rolle,
    ) {
        // Arrange
        innloggetBrukerService.settRoller(roller)

        // Act
        val feil = catchThrowable { tilgangService.validerHarTilgangTilHandling(minimumRolle, "teste") }

        // Assert
        assertThat(feil).isNull()
    }

    @ParameterizedTest(name = "{0} avvises når minimum er {1}")
    @MethodSource("avviser")
    fun avviser(
        roller: Set<Rolle>,
        minimumRolle: Rolle,
    ) {
        // Arrange
        innloggetBrukerService.settRoller(roller)

        // Act
        val feil = catchThrowable { tilgangService.validerHarTilgangTilHandling(minimumRolle, "teste") }

        // Assert
        assertThat(feil).isInstanceOf(ManglerTilgangFeil::class.java)
    }

    companion object {
        @JvmStatic
        fun girTilgang(): List<Arguments> =
            listOf(
                arguments(setOf(VEILEDER), VEILEDER),
                arguments(setOf(SAKSBEHANDLER), VEILEDER),
                arguments(setOf(SAKSBEHANDLER), SAKSBEHANDLER),
                arguments(setOf(BESLUTTER), SAKSBEHANDLER),
                arguments(setOf(BESLUTTER), BESLUTTER),
                arguments(setOf(FORVALTER), FORVALTER),
                arguments(setOf(FORVALTER), VEILEDER),
                arguments(setOf(VEILEDER, FORVALTER), FORVALTER),
                arguments(setOf(FORVALTER, SAKSBEHANDLER), SAKSBEHANDLER),
            )

        @JvmStatic
        fun avviser(): List<Arguments> =
            listOf(
                arguments(emptySet<Rolle>(), VEILEDER),
                arguments(emptySet<Rolle>(), FORVALTER),
                arguments(setOf(VEILEDER), SAKSBEHANDLER),
                arguments(setOf(SAKSBEHANDLER), BESLUTTER),
                arguments(setOf(VEILEDER), FORVALTER),
                arguments(setOf(SAKSBEHANDLER), FORVALTER),
                arguments(setOf(BESLUTTER), FORVALTER),
                arguments(setOf(FORVALTER), SAKSBEHANDLER),
            )
    }
}
