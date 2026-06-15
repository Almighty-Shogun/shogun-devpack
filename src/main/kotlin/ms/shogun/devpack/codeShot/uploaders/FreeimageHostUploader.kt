package ms.shogun.devpack.codeShot.uploaders

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import com.google.gson.JsonObject
import com.google.gson.JsonParser

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShot.CodeShotSecrets
import ms.shogun.devpack.codeShot.HostedImageMultipart

/**
 * Uploads rendered Code Shot images to Freeimage.host.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object FreeimageHostUploader {
    private val httpClient = HttpClient.newHttpClient()

    /**
     * Uploads PNG bytes to Freeimage.host and returns the original uploaded image URL.
     *
     * @param pngBytes Rendered screenshot encoded as PNG.
     *
     * @return Uploaded image URL.
     *
     * @throws IllegalStateException when configuration is incomplete or Freeimage.host rejects the upload.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun upload(pngBytes: ByteArray): String {
        val apiKey = CodeShotSecrets.freeimageHostApiKey()?.takeIf { value ->
            value.isNotBlank()
        } ?: error(message("code-shot.notification.freeimage-host.missing-api-key"))

        val response = httpClient.send(
            request(apiKey, pngBytes),
            HttpResponse.BodyHandlers.ofString()
        )

        if (response.statusCode() !in 200..299) {
            error(message("code-shot.notification.freeimage-host.failed", response.statusCode()))
        }

        return imageUrl(response.body())
    }

    /**
     * Builds the Freeimage.host image upload request.
     *
     * @param apiKey Freeimage.host API key.
     * @param pngBytes Rendered screenshot encoded as PNG.
     *
     * @return HTTP request for the upload.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun request(apiKey: String, pngBytes: ByteArray): HttpRequest {
        val boundary = "ShogunDevPack${System.currentTimeMillis()}"

        return HttpRequest.newBuilder(URI.create("https://freeimage.host/api/1/upload"))
            .header("Content-Type", "multipart/form-data; boundary=$boundary")
            .POST(
                HttpRequest.BodyPublishers.ofByteArray(
                    HostedImageMultipart.body(
                        boundary = boundary,
                        fileFieldName = "source",
                        pngBytes = pngBytes,
                        fields = linkedMapOf(
                            "key" to apiKey,
                            "action" to "upload",
                            "format" to "json"
                        )
                    )
                )
            )
            .build()
    }

    /**
     * Extracts the original uploaded image URL from Freeimage.host's JSON response.
     *
     * @param responseBody Raw Freeimage.host response body.
     *
     * @return Uploaded image URL.
     *
     * @throws IllegalStateException when Freeimage.host returns no image URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun imageUrl(responseBody: String): String {
        val image = runCatching {
            JsonParser.parseString(responseBody).asJsonObject.getObject("image")
        }.getOrNull()

        return image
            ?.getAsString("url")
            ?: error(message("code-shot.notification.freeimage-host.missing-url"))
    }

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
