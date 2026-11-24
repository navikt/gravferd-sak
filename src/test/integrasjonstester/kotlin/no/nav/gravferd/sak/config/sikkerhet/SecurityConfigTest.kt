package no.nav.gravferd.sak.config.sikkerhet

import no.nav.gravferd.sak.AbstractIntegrasjonstest
import no.nav.gravferd.sak.config.sikkerhet.TestTokenServer.token
import no.nav.gravferd.sak.ut.db.fagsak.DefaultFagsakRepository
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SecurityConfigTest(
    @Autowired fagsakRepository: DefaultFagsakRepository,
    @Autowired private val rolleProperties: RolleProperties,
) : AbstractIntegrasjonstest() {
    private val fagsakId = fagsakRepository.opprett().id

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/internal/health/liveness",
            "/internal/info",
            "/internal/prometheus",
            "/v3/api-docs",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
        ],
    )
    fun `åpne endepunkter krever ikke token`(sti: String) {
        mockMvc
            .perform(get(sti))
            .andExpect { assertThat(it.response.status).isNotIn(401, 403) }
    }

    @Test
    fun `beskyttet endepunkt uten token gir 401`() {
        mockMvc
            .perform(get("/api/fagsak/$fagsakId"))
            .andExpect(status().isUnauthorized)
            .andExpect(header().string("WWW-Authenticate", startsWith("Bearer")))
    }

    @Test
    fun `internal-endepunkt utenfor whitelist krever token`() {
        mockMvc
            .perform(get("/internal/annet"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `beskyttet endepunkt med ugyldig token gir 401`() {
        // Arrange
        val token = token(audience = "annen-app", grupper = listOf(rolleProperties.saksbehandler))

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest
    @ValueSource(strings = ["veileder-gruppe", "forvalter-gruppe", "saksbehandler-gruppe", "beslutter-gruppe"])
    fun `hver tillatte AD-gruppe gir tilgang til beskyttet endepunkt`(gruppe: String) {
        // Arrange
        val token = token(grupper = listOf(gruppe))

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `gyldig token uten grupper gir 403`() {
        // Arrange
        val token = token()

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `gyldig token uten groups claim gir 403`() {
        // Arrange
        val token = token(grupper = null)

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `gyldig token med ukjent gruppe gir 403`() {
        // Arrange
        val token = token(grupper = listOf("ukjent-gruppe"))

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `kode6 alene gir ikke tilgang til beskyttet endepunkt`() {
        // Arrange
        val token = token(grupper = listOf(rolleProperties.kode6))

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `kjent gruppe sammen med ukjente grupper gir tilgang`() {
        // Arrange
        val token = token(grupper = listOf("ukjent-gruppe", rolleProperties.saksbehandler))

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `post med gyldig token gir 201 uten csrf-token`() {
        // Arrange
        val token = token(grupper = listOf(rolleProperties.saksbehandler))

        // Act & Assert
        mockMvc
            .perform(post("/api/fagsak").header("Authorization", "Bearer $token"))
            .andExpect(status().isCreated)
    }

    @Test
    fun `post uten tillatt rolle gir 403 selv med gyldig token`() {
        // Arrange
        val token = token()

        // Act & Assert
        mockMvc
            .perform(post("/api/fagsak").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `godkjent kall oppretter ikke sesjon eller autentiserer neste kall`() {
        // Arrange
        val token = token(grupper = listOf(rolleProperties.veileder))

        // Act & Assert
        mockMvc
            .perform(get("/api/fagsak/$fagsakId").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect { assertThat(it.request.getSession(false)).isNull() }

        mockMvc
            .perform(get("/api/fagsak/$fagsakId"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `oppretter ikke sesjon ved avvist kall`() {
        mockMvc
            .perform(get("/api/fagsak/$fagsakId"))
            .andExpect(status().isUnauthorized)
            .andExpect { assertThat(it.request.getSession(false)).isNull() }
    }

    @Test
    fun `endepunkt avviser bruker med lavere rolle enn minimumsrollen`() {
        // Arrange
        val token = token(grupper = listOf(rolleProperties.veileder))

        // Act & Assert
        mockMvc
            .perform(post("/api/fagsak").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `endepunkt slipper inn bruker med høyere rolle enn minimumsrollen`() {
        // Arrange
        val token = token(grupper = listOf(rolleProperties.beslutter))

        // Act & Assert
        mockMvc
            .perform(post("/api/fagsak").header("Authorization", "Bearer $token"))
            .andExpect(status().isCreated)
    }
}
