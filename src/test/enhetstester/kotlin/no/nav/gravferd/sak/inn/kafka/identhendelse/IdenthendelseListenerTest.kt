package no.nav.gravferd.sak.inn.kafka.identhendelse

import no.nav.gravferd.sak.kjerne.personident.IdenthendelseService
import no.nav.person.pdl.aktor.v2.Aktor
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment

class IdenthendelseListenerTest {
    private val listener = IdenthendelseListener(IdenthendelseMapper(), IdenthendelseService())

    @Test
    fun `null record-verdi feiler uten å bekrefte`() {
        var bekreftet = false
        val ack = Acknowledgment { bekreftet = true }
        val record = ConsumerRecord<String, Aktor?>("pdl.aktor-v2", 0, 0L, "syntetisk-aktor-id", null)

        val feil = catchThrowable { listener.listen(listOf(record), ack) }

        assertThat(feil).isInstanceOf(NotImplementedError::class.java)
        assertThat(bekreftet).isFalse()
    }

    @Test
    fun `Kafka-listener har bokstavelig deaktivert auto-start`() {
        val annotation =
            IdenthendelseListener::class.java
                .getMethod("listen", List::class.java, Acknowledgment::class.java)
                .getAnnotation(KafkaListener::class.java)

        assertThat(annotation.autoStartup).isEqualTo("false")
    }
}
