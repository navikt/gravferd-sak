package no.nav.gravferd.sak.ut.rest.pdl

import no.nav.familie.pdl.PdlException
import no.nav.familie.pdl.PdlHybridKlient
import no.nav.gravferd.sak.kjerne.feil.Feil
import no.nav.gravferd.sak.kjerne.feil.IkkeFunnetFeil
import no.nav.gravferd.sak.kjerne.person.Person
import no.nav.gravferd.sak.ut.rest.pdl.dto.HENT_PERSONOPPLYSNINGER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import java.net.URI.create
import java.time.LocalDate

@TestInstance(PER_METHOD)
class DefaultPdlServiceTest {
    private val builder = RestClient.builder()
    private val server = MockRestServiceServer.bindTo(builder).build()
    private val klient = PdlHybridKlient(create("https://pdl.test"), builder.build(), "behandlingsnummer")
    private val pdlService = DefaultPdlService(klient)

    @AfterEach
    fun verifiserServer() {
        server.verify()
    }

    @Test
    fun `henter og mapper personopplysninger`() {
        // Arrange
        server
            .expect(requestTo("https://pdl.test/graphql"))
            .andExpect(jsonPath("$.query").value(HENT_PERSONOPPLYSNINGER))
            .andExpect(jsonPath("$.variables.ident").value("ident"))
            .andExpect(header("Behandlingsnummer", "behandlingsnummer"))
            .andRespond(
                withSuccess(
                    """
                    {"data":{"person":{
                      "navn":[{"fornavn":"Fornavn","mellomnavn":"Mellomnavn","etternavn":"Etternavn"}],
                      "foedselsdato":[{"foedselsdato":"1950-01-31"}],
                      "doedsfall":[{"doedsdato":"2026-11-01"}]
                    }}}
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // Act
        val person = pdlService.hentPerson("ident")

        // Assert
        assertThat(person)
            .isEqualTo(
                Person(
                    navn = "Fornavn Mellomnavn Etternavn",
                    fødselsdato = LocalDate.of(1950, 1, 31),
                    dødsdato = LocalDate.of(2026, 11, 1),
                ),
            )
    }

    @Test
    fun `utelater manglende mellomnavn og gir null for manglende datoer`() {
        // Arrange
        server
            .expect(requestTo("https://pdl.test/graphql"))
            .andRespond(
                withSuccess(
                    """
                    {"data":{"person":{
                      "navn":[{"fornavn":"Fornavn","mellomnavn":null,"etternavn":"Etternavn"}],
                      "foedselsdato":[{"foedselsdato":null}],
                      "doedsfall":[]
                    }}}
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // Act
        val person = pdlService.hentPerson("test-ident-2")

        // Assert
        val forventetPerson = Person(navn = "Fornavn Etternavn", fødselsdato = null, dødsdato = null)
        assertThat(person).isEqualTo(forventetPerson)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notFoundSvar")
    fun `kaster IkkeFunnetFeil når svaret inneholder not_found`(
        beskrivelse: String,
        pdlSvar: String,
    ) {
        // Arrange
        server
            .expect(requestTo("https://pdl.test/graphql"))
            .andRespond(withSuccess(pdlSvar, MediaType.APPLICATION_JSON))

        // Act
        val feil = assertThrows(IkkeFunnetFeil::class.java) { pdlService.hentPerson("ident") }

        // Assert
        assertThat(feil.melding).isEqualTo("Fant ikke person i PDL").doesNotContain("ident")
    }

    @Test
    fun `kaster PdlException videre når ingen feil er not_found`() {
        // Arrange
        server
            .expect(requestTo("https://pdl.test/graphql"))
            .andRespond(
                withSuccess(
                    """{"data":{"person":null},"errors":[{"message":"Ikke tilgang","extensions":{"code":"unauthorized"}}]}""",
                    MediaType.APPLICATION_JSON,
                ),
            )

        // Act
        val feil = assertThrows(PdlException::class.java) { pdlService.hentPerson("ident") }

        // Assert
        assertThat(feil.errors.map { it.extensions?.code }).containsExactly("unauthorized")
    }

    @ParameterizedTest
    @MethodSource("manglendePersonEllerNavnSvar")
    fun `kaster Feil når person eller navn mangler uten feil fra PDL`(
        ident: String,
        pdlSvar: String,
        melding: String,
    ) {
        // Arrange
        server
            .expect(requestTo("https://pdl.test/graphql"))
            .andRespond(withSuccess(pdlSvar, MediaType.APPLICATION_JSON))

        // Act
        val feil = assertThrows(Feil::class.java) { pdlService.hentPerson(ident) }

        // Assert
        assertThat(feil.melding).isEqualTo(melding).doesNotContain(ident)
    }

    @Test
    fun `velger første ikke-null fødselsdato og dødsdato`() {
        // Arrange
        server
            .expect(requestTo("https://pdl.test/graphql"))
            .andRespond(
                withSuccess(
                    """
                    {"data":{"person":{
                      "navn":[{"fornavn":"Fornavn","mellomnavn":null,"etternavn":"Etternavn"}],
                      "foedselsdato":[
                        {"foedselsdato":null},
                        {"foedselsdato":"1950-01-31"},
                        {"foedselsdato":"1960-02-01"}
                      ],
                      "doedsfall":[
                        {"doedsdato":null},
                        {"doedsdato":"2025-03-04"},
                        {"doedsdato":"2026-04-05"}
                      ]
                    }}}
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        // Act
        val person = pdlService.hentPerson("ident")

        // Assert
        val forventetPerson =
            Person(
                navn = "Fornavn Etternavn",
                fødselsdato = LocalDate.of(1950, 1, 31),
                dødsdato = LocalDate.of(2025, 3, 4),
            )

        assertThat(person).isEqualTo(forventetPerson)
    }

    companion object {
        @JvmStatic
        fun notFoundSvar(): List<Arguments> =
            listOf(
                Arguments.of(
                    "en feil",
                    """{"data":{"person":null},"errors":[{"message":"Fant ikke person","extensions":{"code":"not_found"}}]}""",
                ),
                Arguments.of(
                    "flere feil med not_found sist",
                    """{"data":{"person":null},"errors":[{"message":"Ikke tilgang","extensions":{"code":"unauthorized"}},{"message":"Fant ikke person","extensions":{"code":"not_found"}}]}""",
                ),
            )

        @JvmStatic
        fun manglendePersonEllerNavnSvar(): List<Arguments> =
            listOf(
                Arguments.of("test-ident-mangler-person", """{"data":{"person":null}}""", "Svar fra PDL mangler person"),
                Arguments.of(
                    "test-ident-mangler-navn",
                    """{"data":{"person":{"navn":[],"foedselsdato":[],"doedsfall":[]}}}""",
                    "Person i PDL mangler navn",
                ),
            )
    }
}
