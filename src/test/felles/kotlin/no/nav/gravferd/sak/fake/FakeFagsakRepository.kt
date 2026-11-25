package no.nav.gravferd.sak.fake

import no.nav.gravferd.sak.common.ClockProvider
import no.nav.gravferd.sak.kjerne.fagsak.Fagsak
import no.nav.gravferd.sak.kjerne.fagsak.FagsakRepository
import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.springframework.dao.OptimisticLockingFailureException
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class FakeFagsakRepository(
    private val clockProvider: ClockProvider = FakeClockProvider(),
    private val hentSaksbehandler: () -> String = { "VL" },
) : FagsakRepository {
    private val nesteId = AtomicLong(1)
    private val fagsaker = ConcurrentHashMap<Long, Fagsak>()

    override fun opprett(): Fagsak {
        val saksbehandler = hentSaksbehandler()
        val tidspunkt = LocalDateTime.now(clockProvider.get())
        val fagsak =
            Fagsak(
                id = nesteId.getAndIncrement(),
                metadata =
                    Entitetsmetadata(
                        versjon = 1,
                        opprettetAv = saksbehandler,
                        opprettetTidspunkt = tidspunkt,
                        endretAv = saksbehandler,
                        endretTidspunkt = tidspunkt,
                    ),
            )
        fagsaker[fagsak.id] = fagsak
        return fagsak
    }

    override fun oppdater(fagsak: Fagsak): Fagsak {
        val lagret =
            checkNotNull(
                fagsaker.compute(fagsak.id) { _, eksisterende ->
                    if (eksisterende == null || eksisterende.metadata.versjon != fagsak.metadata.versjon) {
                        throw OptimisticLockingFailureException("Fagsak ${fagsak.id} har endret seg")
                    }

                    eksisterende.copy(
                        metadata =
                            fagsak.metadata.copy(
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
                    opprettetAv = fagsak.metadata.opprettetAv,
                    opprettetTidspunkt = fagsak.metadata.opprettetTidspunkt,
                ),
        )
    }

    override fun hent(id: Long): Fagsak? = fagsaker[id]
}
