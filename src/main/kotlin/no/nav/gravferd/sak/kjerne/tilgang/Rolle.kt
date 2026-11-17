package no.nav.gravferd.sak.kjerne.tilgang

enum class Rolle(
    val nivå: Int,
) {
    BESLUTTER(4),
    SAKSBEHANDLER(3),
    FORVALTER(2),
    VEILEDER(1),
}
