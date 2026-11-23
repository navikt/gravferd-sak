package no.nav.gravferd.sak.kjerne.behandling

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import no.nav.gravferd.sak.kjerne.felles.HarEntitetsmetadata

data class Behandling(
    val id: Long,
    val fagsakId: Long,
    override val metadata: Entitetsmetadata,
) : HarEntitetsmetadata
