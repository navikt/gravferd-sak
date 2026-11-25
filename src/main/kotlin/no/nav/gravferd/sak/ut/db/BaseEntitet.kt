package no.nav.gravferd.sak.ut.db

import no.nav.gravferd.sak.kjerne.felles.Entitetsmetadata
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import java.time.LocalDateTime

abstract class BaseEntitet {
    @CreatedBy
    @InsertOnlyProperty
    @Column("opprettet_av")
    var opprettetAv: String? = null

    @CreatedDate
    @InsertOnlyProperty
    @Column("opprettet_tidspunkt")
    var opprettetTidspunkt: LocalDateTime? = null

    @LastModifiedBy
    @Column("endret_av")
    var endretAv: String? = null

    @LastModifiedDate
    @Column("endret_tidspunkt")
    var endretTidspunkt: LocalDateTime? = null

    @Version
    @Column("versjon")
    var versjon: Long = 0

    fun tilMetadata(): Entitetsmetadata =
        Entitetsmetadata(
            versjon = versjon,
            opprettetAv =
                checkNotNull(opprettetAv) {
                    "Entiteten mangler opprettetAv"
                },
            opprettetTidspunkt =
                checkNotNull(opprettetTidspunkt) {
                    "Entiteten mangler opprettetTidspunkt"
                },
            endretAv = endretAv,
            endretTidspunkt = endretTidspunkt,
        )
}
