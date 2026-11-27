package no.nav.gravferd.sak.config.sikkerhet

import no.nav.familie.felles.klient.KlientTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KlientTokenConfig {
    @Bean
    fun klientTokenProvider() = KlientTokenProvider { SikkerhetContext.hentJwt()?.tokenValue }
}
