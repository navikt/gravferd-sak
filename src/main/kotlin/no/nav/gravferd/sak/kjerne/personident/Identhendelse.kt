package no.nav.gravferd.sak.kjerne.personident

data class Identhendelse(
    val identifikatorer: List<Identifikator>,
) {
    data class Identifikator(
        val idnummer: String,
        val type: Type,
        val gjeldende: Boolean,
    )

    enum class Type {
        FOLKEREGISTERIDENT,
        AKTORID,
        NPID,
    }
}
