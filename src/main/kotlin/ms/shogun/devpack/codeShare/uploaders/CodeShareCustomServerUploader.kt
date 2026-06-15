package ms.shogun.devpack.codeShare.uploaders

import java.net.http.HttpClient
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShare.CodeShareResult
import ms.shogun.devpack.codeShare.CodeShareRequest
import ms.shogun.devpack.codeShare.CodeShareUploader
import ms.shogun.devpack.codeShare.CodeShareException
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.utils.customServer.CustomServerRequest
import ms.shogun.devpack.utils.customServer.ResponseUrlFormatter
import ms.shogun.devpack.utils.customServer.CustomServerFilePart

/**
 * Code sharing uploader that uploads code to a configured custom endpoint.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShareCustomServerUploader : CodeShareUploader {
    private val httpClient = HttpClient.newHttpClient()

    override fun upload(request: CodeShareRequest): CodeShareResult {
        val settings = ShogunDevPackSettings.instance
        val requestUrl = settings.codeShareCustomServerUrl?.takeIf { url ->
            url.isNotBlank()
        } ?: throw CodeShareException(message("code-share.custom-server.error.missing-url"))

        val response = httpClient.send(
            CustomServerRequest.multipart(
                requestUrl = requestUrl,
                method = settings.codeShareCustomServerMethod,
                urlParameters = settings.codeShareCustomServerUrlParameters,
                headers = settings.codeShareCustomServerRequestHeaders,
                bodyFields = settings.codeShareCustomServerRequestBody,
                filePart = CustomServerFilePart(
                    fieldName = settings.codeShareCustomServerFileFormName,
                    fileName = request.fileName,
                    contentType = "text/plain; charset=utf-8",
                    bytes = request.content.toByteArray(StandardCharsets.UTF_8)
                )
            ),
            HttpResponse.BodyHandlers.ofString(),
        )

        if (response.statusCode() !in 200..299) {
            throw CodeShareException(message("code-share.custom-server.error.upload-failed", response.statusCode()))
        }

        return CodeShareResult(
            ResponseUrlFormatter.format(
                settings.codeShareCustomServerUrlTemplate,
                response.body()
            )
        )
    }
}
