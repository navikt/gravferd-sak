package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.kjerne.tilgang.InnloggetBrukerService
import no.nav.gravferd.sak.kjerne.tilgang.Rolle

class FakeInnloggetBrukerService : InnloggetBrukerService {
    private val roller = ThreadLocal.withInitial { emptySet<Rolle>() }

    override fun hentRoller(): Set<Rolle> = roller.get()

    fun settRoller(vararg roller: Rolle) = this.roller.set(roller.toSet())

    fun settRoller(roller: Collection<Rolle>) = this.roller.set(roller.toSet())

    fun nullstill() = roller.remove()
}
