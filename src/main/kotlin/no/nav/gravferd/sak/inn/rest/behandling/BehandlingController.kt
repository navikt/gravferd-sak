package no.nav.gravferd.sak.inn.rest.behandling

import no.nav.gravferd.sak.kjerne.behandling.BehandlingService
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.SAKSBEHANDLER
import no.nav.gravferd.sak.kjerne.tilgang.Rolle.VEILEDER
import no.nav.gravferd.sak.kjerne.tilgang.TilgangService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/behandling")
class BehandlingController(
    private val behandlingService: BehandlingService,
    private val tilgangService: TilgangService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun opprett(
        @RequestBody request: OpprettBehandlingRequest,
    ): BehandlingDto {
        tilgangService.validerHarTilgangTilHandling(
            minimumRolle = SAKSBEHANDLER,
            handling = "opprette behandling",
        )

        return BehandlingDto.fraDomene(behandlingService.opprett(request.fagsakId))
    }

    @GetMapping("/{behandlingId}")
    fun hent(
        @PathVariable behandlingId: Long,
    ): BehandlingDto {
        tilgangService.validerHarTilgangTilHandling(
            minimumRolle = VEILEDER,
            handling = "hente behandling",
        )

        return BehandlingDto.fraDomene(behandlingService.hent(behandlingId))
    }

    @GetMapping("/fagsak/{fagsakId}")
    fun hentForFagsak(
        @PathVariable fagsakId: Long,
    ): List<BehandlingDto> {
        tilgangService.validerHarTilgangTilHandling(
            minimumRolle = VEILEDER,
            handling = "hente behandlinger for fagsak",
        )

        return behandlingService.hentForFagsak(fagsakId).map { BehandlingDto.fraDomene(it) }
    }
}
