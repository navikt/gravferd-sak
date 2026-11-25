package no.nav.gravferd.sak.kjerne.personident

import no.nav.gravferd.sak.kjerne.personident.Identhendelse.Identifikator
import no.nav.gravferd.sak.kjerne.personident.Identhendelse.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IdenthendelseTest {
    @Test
    fun `identifikator har feltbasert equals og hashCode`() {
        val identifikator = Identifikator("syntetisk-id-1", Type.AKTORID, true)
        val likIdentifikator = Identifikator("syntetisk-id-1", Type.AKTORID, true)

        assertThat(identifikator).isEqualTo(likIdentifikator)
        assertThat(identifikator.hashCode()).isEqualTo(likIdentifikator.hashCode())
        assertThat(identifikator).isNotEqualTo(Identifikator("syntetisk-id-2", Type.AKTORID, true))
        assertThat(identifikator).isNotEqualTo(Identifikator("syntetisk-id-1", Type.FOLKEREGISTERIDENT, true))
        assertThat(identifikator).isNotEqualTo(Identifikator("syntetisk-id-1", Type.AKTORID, false))
    }

    @Test
    fun `endret idnummer gir ulik identifikator`() {
        val identifikator = Identifikator("syntetisk-id-1", Type.FOLKEREGISTERIDENT, true)
        val identifikatorMedEndretIdnummer = identifikator.copy(idnummer = "syntetisk-id-2")

        assertThat(identifikatorMedEndretIdnummer).isNotEqualTo(identifikator)
    }

    @Test
    fun `copy bevarer felt og kan endre gjeldende`() {
        val identifikator = Identifikator("syntetisk-id-1", Type.FOLKEREGISTERIDENT, true)

        val kopi = identifikator.copy(gjeldende = false)

        assertThat(kopi).isEqualTo(Identifikator("syntetisk-id-1", Type.FOLKEREGISTERIDENT, false))
    }

    @Test
    fun `identhendelse har feltbasert equals og hashCode`() {
        val hendelse = Identhendelse(listOf(Identifikator("syntetisk-aktor-id", Type.AKTORID, true)))
        val likHendelse = Identhendelse(listOf(Identifikator("syntetisk-aktor-id", Type.AKTORID, true)))

        assertThat(hendelse).isEqualTo(likHendelse)
        assertThat(hendelse.hashCode()).isEqualTo(likHendelse.hashCode())
        assertThat(hendelse).isNotEqualTo(Identhendelse(listOf(Identifikator("syntetisk-annet-id", Type.AKTORID, true))))
    }
}
