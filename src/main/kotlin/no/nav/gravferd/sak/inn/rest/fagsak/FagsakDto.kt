package no.nav.gravferd.sak.inn.rest.fagsak

import no.nav.gravferd.sak.inn.rest.EntitetsmetadataDto
import no.nav.gravferd.sak.kjerne.fagsak.Fagsak

data class FagsakDto(
    val id: Long,
    val metadata: EntitetsmetadataDto,
) {
    companion object {
        fun fraDomene(fagsak: Fagsak): FagsakDto = FagsakDto(id = fagsak.id, metadata = EntitetsmetadataDto.fraDomene(fagsak.metadata))
    }
}
