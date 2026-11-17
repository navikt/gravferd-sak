package no.nav.gravferd.sak.kjerne.tilgang

import no.nav.gravferd.sak.kjerne.feil.ManglerTilgangFeil
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.BESLUTTER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.FORVALTER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.SAKSBEHANDLER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.VEILEDER
import org.springframework.stereotype.Service

@Service
class TilgangService(
    private val innloggetBrukerService: InnloggetBrukerService,
) {
    fun validerHarTilgangTilHandling(
        minimumRolle: Rolle,
        handling: String,
    ) {
        val roller = innloggetBrukerService.hentRoller()
        val harTilgang =
            when (minimumRolle) {
                FORVALTER -> FORVALTER in roller
                VEILEDER, SAKSBEHANDLER, BESLUTTER -> roller.any { it.nivå >= minimumRolle.nivå }
            }

        if (!harTilgang) {
            throw ManglerTilgangFeil("Mangler tilgang til å $handling. Krever $minimumRolle, har $roller")
        }
    }
}
