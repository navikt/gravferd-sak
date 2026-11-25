package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.common.ClockProvider
import no.nav.gravferd.sak.kjerne.behandling.Behandling
import no.nav.gravferd.sak.kjerne.behandling.BehandlingRepository
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.springframework.dao.OptimisticLockingFailureException
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class FakeBehandlingRepository(
    private val clockProvider: ClockProvider = FakeClockProvider(),
    private val hentSaksbehandler: () -> String = { "VL" },
) : BehandlingRepository {
    private val nesteId = AtomicLong(1)
    private val behandlinger = ConcurrentHashMap<Long, Behandling>()

    override fun opprett(fagsakId: Long): Behandling {
        val saksbehandler = hentSaksbehandler()
        val tidspunkt = LocalDateTime.now(clockProvider.get())
        val behandling =
            Behandling(
                id = nesteId.getAndIncrement(),
                fagsakId = fagsakId,
                metadata =
                    Entitetsmetadata(
                        versjon = 1,
                        opprettetAv = saksbehandler,
                        opprettetTidspunkt = tidspunkt,
                        endretAv = saksbehandler,
                        endretTidspunkt = tidspunkt,
                    ),
            )
        behandlinger[behandling.id] = behandling
        return behandling
    }

    override fun oppdater(behandling: Behandling): Behandling {
        val lagret =
            checkNotNull(
                behandlinger.compute(behandling.id) { _, eksisterende ->
                    if (eksisterende == null || eksisterende.metadata.versjon != behandling.metadata.versjon) {
                        throw OptimisticLockingFailureException("Behandling ${behandling.id} har endret seg")
                    }

                    eksisterende.copy(
                        fagsakId = behandling.fagsakId,
                        metadata =
                            behandling.metadata.copy(
                                versjon = eksisterende.metadata.versjon + 1,
                                opprettetAv = eksisterende.metadata.opprettetAv,
                                opprettetTidspunkt = eksisterende.metadata.opprettetTidspunkt,
                                endretAv = hentSaksbehandler(),
                                endretTidspunkt = LocalDateTime.now(clockProvider.get()),
                            ),
                    )
                },
            )

        // Spring Data JDBC beholder de innsendte InsertOnlyProperty-feltene i returverdien.
        return lagret.copy(
            metadata =
                lagret.metadata.copy(
                    opprettetAv = behandling.metadata.opprettetAv,
                    opprettetTidspunkt = behandling.metadata.opprettetTidspunkt,
                ),
        )
    }

    override fun hent(id: Long): Behandling? = behandlinger[id]

    override fun hentForFagsak(fagsakId: Long): List<Behandling> = behandlinger.values.filter { it.fagsakId == fagsakId }
}
