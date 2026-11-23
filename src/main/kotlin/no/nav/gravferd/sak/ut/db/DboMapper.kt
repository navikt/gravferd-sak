package no.nav.gravferd.sak.ut.db

import no.nav.gravferd.sak.kjerne.felles.HarEntitetsmetadata

abstract class DboMapper<D : HarEntitetsmetadata, T : BaseEntitet> {
    fun tilDbo(domene: D): T = lagDboUtenMetadata(domene).medMetadata(domene.metadata)

    protected abstract fun lagDboUtenMetadata(domene: D): T
}
