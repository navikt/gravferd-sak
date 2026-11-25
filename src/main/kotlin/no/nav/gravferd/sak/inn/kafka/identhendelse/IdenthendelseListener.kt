package no.nav.gravferd.sak.inn.kafka.identhendelse

import no.nav.gravferd.sak.kjerne.personident.IdenthendelseService
import no.nav.person.pdl.aktor.v2.Aktor
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "kafka.identhendelser", name = ["enabled"], havingValue = "true", matchIfMissing = false)
class IdenthendelseListener(
    private val identhendelseMapper: IdenthendelseMapper,
    private val identhendelseService: IdenthendelseService,
) {
    @KafkaListener(
        id = "gravferd-sak-aktorv2",
        groupId = "gravferd-sak-aktorv2-group",
        topics = ["pdl.aktor-v2"],
        containerFactory = "identhendelseContainerFactory",
        autoStartup = "false",
    )
    fun listen(
        records: List<ConsumerRecord<String, Aktor?>>,
        ack: Acknowledgment,
    ): Unit = TODO("Map og deleger hver record; bekreft bare ferdigbehandlet arbeid")
}
