package no.nav.gravferd.sak.kjerne.fagsak

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import no.nav.gravferd.sak.kjerne.felles.HarEntitetsmetadata

data class Fagsak(
    val id: Long,
    override val metadata: Entitetsmetadata,
) : HarEntitetsmetadata
