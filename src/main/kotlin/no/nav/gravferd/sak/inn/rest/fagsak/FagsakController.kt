package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.kjerne.fagsak.FagsakService
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.SAKSBEHANDLER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.VEILEDER
import no.nav.gravferd.sak.kjerne.tilgang.TilgangService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fagsak")
class FagsakController(
    private val fagsakService: FagsakService,
    private val tilgangService: TilgangService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun opprett(): FagsakDto {
        tilgangService.validerHarTilgangTilHandling(
            minimumRolle = SAKSBEHANDLER,
            handling = "opprette fagsak",
        )

        return fagsakService.opprett().tilDto()
    }

    @GetMapping("/{fagsakId}")
    fun hent(
        @PathVariable fagsakId: Long,
    ): FagsakDto {
        tilgangService.validerHarTilgangTilHandling(
            minimumRolle = VEILEDER,
            handling = "hente fagsak",
        )

        return fagsakService.hent(fagsakId).tilDto()
    }
}
