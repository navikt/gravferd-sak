package no.nav.gravferd.sak.config.implementasjon

import jakarta.servlet.http.HttpServletRequest
import no.nav.familie.log.mdc.MDCConstants
import no.nav.gravferd.sak.common.ClockProvider
import no.nav.gravferd.sak.config.sikkerhet.SikkerhetContext
import no.nav.gravferd.sak.config.sikkerhet.SikkerhetContext.hentSaksbehandler
import no.nav.gravferd.sak.kjerne.tilgang.AuditLogger
import no.nav.gravferd.sak.kjerne.tilgang.Sporingsdata
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Service
class DefaultAuditLogger(
    private val clockProvider: ClockProvider,
) : AuditLogger {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val audit = LoggerFactory.getLogger(AUDIT_LOGGER)

    override fun log(data: Sporingsdata) {
        val request = getRequest() ?: error("Ikke brukt i context av en HTTP request")

        if (!SikkerhetContext.erMaskinTilMaskinToken()) {
            audit.info(createAuditLogString(data, request))
        } else {
            logger.debug("Maskin til maskin token i request")
        }
    }

    private fun getRequest(): HttpServletRequest? =
        RequestContextHolder
            .getRequestAttributes()
            ?.takeIf { it is ServletRequestAttributes }
            ?.let { it as ServletRequestAttributes }
            ?.request

    private fun createAuditLogString(
        data: Sporingsdata,
        request: HttpServletRequest,
    ): String =
        lagCefLinje(
            data = data,
            tidspunkt = clockProvider.get().millis(),
            suid = hentSaksbehandler(),
            callId = getCallId(),
            requestMethod = request.method,
            requestUri = request.requestURI,
        )

    private fun getCallId(): String = MDC.get(MDCConstants.MDC_CALL_ID)?.takeIf { it.isNotBlank() } ?: error("Mangler callId")

    companion object {
        const val AUDIT_LOGGER = "auditLogger"
    }
}

internal fun lagCefLinje(
    data: Sporingsdata,
    tidspunkt: Long,
    suid: String,
    callId: String,
    requestMethod: String,
    requestUri: String,
): String {
    val version = "0"
    val deviceVendor = "Gravferd sak"
    val deviceProduct = "gravferd-sak"
    val deviceVersion = "1.0"
    val deviceEventClassId = "audit:${data.event.type}"
    val name = "Sporingslogg"
    val severity = "INFO"

    val header =
        listOf(
            "CEF:$version",
            deviceVendor,
            deviceProduct,
            deviceVersion,
            deviceEventClassId,
            name,
            severity,
        ).joinToString("|")

    val extension =
        listOfNotNull(
            "end" to tidspunkt.toString(),
            "suid" to suid,
            "duid" to data.personIdent,
            "sproc" to callId,
            "requestMethod" to requestMethod,
            "request" to requestUri,
            data.custom1?.let { "cs3Label" to it.key },
            data.custom1?.let { "cs3" to it.value },
            data.custom2?.let { "cs5Label" to it.key },
            data.custom2?.let { "cs5" to it.value },
            data.custom3?.let { "cs6Label" to it.key },
            data.custom3?.let { "cs6" to it.value },
        ).joinToString(" ") { (key, value) -> "$key=${escapeExtension(value)}" }

    return "$header|$extension"
}

private fun escapeExtension(verdi: String): String =
    verdi
        .replace("\\", "\\\\")
        .replace("=", "\\=")
        .replace("\r", "\\r")
        .replace("\n", "\\n")
