package ms.shogun.devpack.codeShot.uploaders

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

import com.google.gson.JsonObject
import com.google.gson.JsonParser

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShot.CodeShotSecrets
import ms.shogun.devpack.codeShot.HostedImageMultipart

/**
 * Uploads rendered Code Shot images to ImgBB.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object ImgBbUploader {
    private val httpClient = HttpClient.newHttpClient()

    /**
     * Uploads PNG bytes to ImgBB and returns the original uploaded image URL.
     *
     * @param pngBytes Rendered screenshot encoded as PNG.
     *
     * @return Uploaded image URL.
     *
     * @throws IllegalStateException when configuration is incomplete or ImgBB rejects the upload.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun upload(pngBytes: ByteArray): String {
        val apiKey = CodeShotSecrets.imgbbApiKey()?.takeIf { value ->
            value.isNotBlank()
        } ?: error(message("code-shot.notification.imgbb.missing-api-key"))

        val response = httpClient.send(
            request(apiKey, pngBytes),
            HttpResponse.BodyHandlers.ofString()
        )

        if (response.statusCode() !in 200..299) {
            error(message("code-shot.notification.imgbb.failed", response.statusCode()))
        }

        return imageUrl(response.body())
    }

    /**
     * Builds the ImgBB image upload request.
     *
     * @param apiKey ImgBB API key.
     * @param pngBytes Rendered screenshot encoded as PNG.
     *
     * @return HTTP request for the upload.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun request(apiKey: String, pngBytes: ByteArray): HttpRequest {
        val boundary = "ShogunDevPack${System.currentTimeMillis()}"

        return HttpRequest.newBuilder(URI.create("https://api.imgbb.com/1/upload?key=${encode(apiKey)}"))
            .header("Content-Type", "multipart/form-data; boundary=$boundary")
            .POST(
                HttpRequest.BodyPublishers.ofByteArray(
                    HostedImageMultipart.body(
                        boundary = boundary,
                        fileFieldName = "image",
                        pngBytes = pngBytes
                    )
                )
            )
            .build()
    }

    /**
     * Extracts the original uploaded image URL from ImgBB's JSON response.
     *
     * @param responseBody Raw ImgBB response body.
     *
     * @return Uploaded image URL.
     *
     * @throws IllegalStateException when ImgBB returns no image URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun imageUrl(responseBody: String): String {
        val data = runCatching {
            JsonParser.parseString(responseBody).asJsonObject.getAsJsonObject("data")
        }.getOrNull()

        return data
            ?.getAsString("url")
            ?: data?.getObject("image")?.getAsString("url")
            ?: error(message("code-shot.notification.imgbb.missing-url"))
    }

    /**
     * URL-encodes a query parameter value.
     *
     * @param value Raw value.
     *
     * @return Encoded value.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8)

    /**
     * Reads a non-blank string property from a JSON object.
     *
     * @param name Property name.
     *
     * @return String value, or `null` when absent.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun JsonObject.getAsString(name: String): String? =
        get(name)
            ?.takeIf { element ->
                element.isJsonPrimitive
            }
            ?.asString
            ?.takeIf { value ->
                value.isNotBlank()
            }

    /**
     * Reads an object property from a JSON object.
     *
     * @param name Property name.
     *
     * @return JSON object, or `null` when absent.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun JsonObject.getObject(name: String): JsonObject? =
        get(name)
            ?.takeIf { element ->
                element.isJsonObject
            }
            ?.asJsonObject
}
