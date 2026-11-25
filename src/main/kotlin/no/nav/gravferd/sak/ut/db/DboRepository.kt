package no.nav.gravferd.sak.ut.db

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.relational.core.query.Query

abstract class DboRepository<T : BaseEntitet, ID : Any>(
    private val jdbcAggregateOperations: JdbcAggregateOperations,
    private val dboType: Class<T>,
) {
    protected fun opprettDbo(dbo: T): T = jdbcAggregateOperations.insert(dbo)

    protected fun oppdaterDbo(dbo: T): T = jdbcAggregateOperations.update(dbo)

    protected fun hentDbo(id: ID): T? = jdbcAggregateOperations.findById(id, dboType)

    protected fun finnAlleDbo(query: Query): List<T> = jdbcAggregateOperations.findAll(query, dboType).toList()
}
