package no.nav.gravferd.sak.config

import no.nav.gravferd.sak.inn.kafka.identhendelse.IdenthendelseListener
import no.nav.gravferd.sak.inn.kafka.identhendelse.IdenthendelseMapper
import no.nav.gravferd.sak.kjerne.personident.IdenthendelseService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class KafkaDeaktivertTest {
    private val contextRunner =
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration::class.java))
            .withUserConfiguration(
                IdenthendelseListener::class.java,
                IdenthendelseMapper::class.java,
                IdenthendelseService::class.java,
            ).withPropertyValues("spring.kafka.listener.auto-startup=false")

    @Test
    fun `listener er fraværende når enabled mangler`() {
        contextRunner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).doesNotHaveBean(IdenthendelseListener::class.java)
        }
    }

    @Test
    fun `listener er fraværende når enabled er false`() {
        contextRunner
            .withPropertyValues("kafka.identhendelser.enabled=false")
            .run { context ->
                assertThat(context).hasNotFailed()
                assertThat(context).doesNotHaveBean(IdenthendelseListener::class.java)
            }
    }
}
