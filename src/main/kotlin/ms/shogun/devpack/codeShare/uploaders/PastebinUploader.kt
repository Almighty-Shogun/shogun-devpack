package ms.shogun.devpack.codeShare.uploaders

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShare.CodeShareResult
import ms.shogun.devpack.codeShare.CodeShareRequest
import ms.shogun.devpack.codeShare.CodeShareUploader
import ms.shogun.devpack.codeShare.CodeShareException
import ms.shogun.devpack.codeShare.pastebin.PastebinSyntax
import ms.shogun.devpack.codeShare.pastebin.PastebinVisibility

/**
 * Code sharing uploader that creates Pastebin pastes.
 *
 * @param developerKey Pastebin API developer key.
 * @param userKey Optional Pastebin user key for account/private uploads.
 * @param visibility Paste visibility mode.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class PastebinUploader(
    private val developerKey: String?,
    private val userKey: String?,
    private val visibility: PastebinVisibility
) : CodeShareUploader {
    private val httpClient = HttpClient.newHttpClient()

    override fun upload(request: CodeShareRequest): CodeShareResult {
        val apiDeveloperKey = developerKey?.takeIf { value ->
            value.isNotBlank()
        } ?: throw CodeShareException(message("code-share.pastebin.error.missing-developer-key"))

        if (visibility == PastebinVisibility.PRIVATE && userKey.isNullOrBlank()) {
            throw CodeShareException(message("code-share.pastebin.error.missing-user-key"))
        }

        val response = sendPasteRequest(request, apiDeveloperKey, includeSyntaxFormat = true)
            .let { firstResponse ->
                if (firstResponse.hasInvalidSyntaxFormat() && request.syntaxName != null) {
                    sendPasteRequest(request, apiDeveloperKey, includeSyntaxFormat = false)
                } else {
                    firstResponse
                }
            }

        if (!response.isSuccessfulPaste()) {
            throw CodeShareException(message("code-share.pastebin.error.upload-failed"))
        }

        return CodeShareResult(response.body().trim())
    }

    /**
     * Builds the Pastebin API request.
     *
     * @param request Code share request.
     * @param developerKey Pastebin developer key.
     * @param includeSyntaxFormat Whether the request may include `api_paste_format`.
     *
     * @return HTTP request for creating a paste.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun pasteRequest(request: CodeShareRequest, developerKey: String, includeSyntaxFormat: Boolean): HttpRequest =
        HttpRequest.newBuilder(URI.create("https://pastebin.com/api/api_post.php"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(formBody(request, developerKey, includeSyntaxFormat)))
            .build()

    /**
     * Sends the Pastebin API request.
     *
     * @param request Code share request.
     * @param developerKey Pastebin developer key.
     * @param includeSyntaxFormat Whether the request may include `api_paste_format`.
     *
     * @return Pastebin HTTP response.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun sendPasteRequest(
        request: CodeShareRequest,
        developerKey: String,
        includeSyntaxFormat: Boolean
    ): HttpResponse<String> =
        httpClient.send(
            pasteRequest(request, developerKey, includeSyntaxFormat),
            HttpResponse.BodyHandlers.ofString(),
        )

    /**
     * Builds the URL-encoded Pastebin form payload.
     *
     * @param request Code share request.
     * @param developerKey Pastebin developer key.
     * @param includeSyntaxFormat Whether the request may include `api_paste_format`.
     *
     * @return URL-encoded form body.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun formBody(request: CodeShareRequest, developerKey: String, includeSyntaxFormat: Boolean): String {
        val values = linkedMapOf(
            "api_dev_key" to developerKey,
            "api_option" to "paste",
            "api_paste_name" to request.fileName,
            "api_paste_code" to request.content,
            "api_paste_private" to visibility.apiValue
        )

        PastebinSyntax.fromExtension(request.syntaxName)?.takeIf {
            includeSyntaxFormat
        }?.let { syntaxName ->
            values["api_paste_format"] = syntaxName
        }

        userKey?.takeIf { key ->
            key.isNotBlank()
        }?.let { key ->
            values["api_user_key"] = key
        }

        return values.entries.joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(value)}"
        }
    }

    /**
     * Encodes a value for a URL-encoded form body.
     *
     * @param value Raw form value.
     *
     * @return URL-encoded form value.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8)

    /**
     * Checks whether Pastebin created a paste successfully.
     *
     * @return `true` when the response contains a paste URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun HttpResponse<String>.isSuccessfulPaste(): Boolean =
        statusCode() in 200..299 && body().startsWith("http")

    /**
     * Checks whether Pastebin rejected the configured syntax format.
     *
     * @return `true` when the response indicates an invalid `api_paste_format`.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun HttpResponse<String>.hasInvalidSyntaxFormat(): Boolean =
        body().contains("invalid api_paste_format", ignoreCase = true)
}
