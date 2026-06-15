package ms.shogun.devpack.codeShot.uploaders

import java.net.http.HttpClient
import java.net.http.HttpResponse

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.utils.customServer.CustomServerRequest
import ms.shogun.devpack.utils.customServer.CustomServerFilePart
import ms.shogun.devpack.utils.customServer.ResponseUrlFormatter

/**
 * Uploads rendered Code Shot images to a configured custom server.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodeShotCustomServerUploader {
    private val httpClient = HttpClient.newHttpClient()

    /**
     * Uploads PNG bytes to the configured endpoint and returns the copied image URL.
     *
     * @param pngBytes Rendered screenshot encoded as PNG.
     *
     * @return Image URL resolved from the configured response template.
     *
     * @throws IllegalStateException when configuration is incomplete or the upload fails.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun upload(pngBytes: ByteArray): String {
        val settings = ShogunDevPackSettings.instance
        val requestUrl = settings.codeShotCustomServerUrl?.takeIf { url ->
            url.isNotBlank()
        } ?: error(message("code-shot.notification.custom-server.missing-url"))

        val response = httpClient.send(
            CustomServerRequest.multipart(
                requestUrl = requestUrl,
                method = settings.codeShotCustomServerMethod,
                urlParameters = settings.codeShotCustomServerUrlParameters,
                headers = settings.codeShotCustomServerRequestHeaders,
                bodyFields = settings.codeShotCustomServerRequestBody,
                filePart = CustomServerFilePart(
                    fieldName = settings.codeShotCustomServerFileFormName,
                    fileName = "code-shot.png",
                    contentType = "image/png",
                    bytes = pngBytes
                )
            ),
            HttpResponse.BodyHandlers.ofString(),
        )

        if (response.statusCode() !in 200..299) {
            error(message("code-shot.notification.custom-server.failed", response.statusCode()))
        }

        return ResponseUrlFormatter.format(
            settings.codeShotCustomServerImageUrlTemplate,
            response.body()
        )
    }
}
