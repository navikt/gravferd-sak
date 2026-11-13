package no.nav.gravferd.sak.config.implementasjon

import no.nav.gravferd.sak.config.sikkerhet.SikkerhetContext
import no.nav.gravferd.sak.kjerne.tilgang.InnloggetBrukerService
import no.nav.gravferd.sak.kjerne.tilgang.Rolle
import org.springframework.stereotype.Component

@Component
class DefaultInnloggetBrukerService : InnloggetBrukerService {
    override fun hentRoller(): Set<Rolle> = SikkerhetContext.hentRoller()
}
