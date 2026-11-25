package no.nav.gravferd.sak.kjerne.person

import java.time.LocalDate

data class Person(
    val navn: String,
    val fødselsdato: LocalDate?,
    val dødsdato: LocalDate?,
)
