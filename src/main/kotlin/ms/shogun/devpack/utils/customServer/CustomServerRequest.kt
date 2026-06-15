package ms.shogun.devpack.utils.customServer

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpRequest
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

/**
 * Builds multipart HTTP requests for custom server uploads.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CustomServerRequest {
    /**
     * Builds a multipart request with query parameters, custom headers, text fields, and one file part.
     *
     * @param requestUrl Base request URL.
     * @param method HTTP method.
     * @param urlParameters Persisted key/value URL parameters.
     * @param headers Persisted key/value request headers.
     * @param bodyFields Persisted key/value multipart text fields.
     * @param filePart Multipart file part.
     *
     * @return HTTP request ready to send.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun multipart(
        requestUrl: String,
        method: String,
        urlParameters: String?,
        headers: String?,
        bodyFields: String?,
        filePart: CustomServerFilePart
    ): HttpRequest {
        val boundary = "ShogunDevPack${System.currentTimeMillis()}"
        val requestBuilder = HttpRequest.newBuilder(uriWithParameters(requestUrl, urlParameters))
            .method(
                HttpMethod.from(method).name,
                HttpRequest.BodyPublishers.ofByteArray(multipartBody(boundary, bodyFields, filePart)),
            )
            .header("Content-Type", "multipart/form-data; boundary=$boundary")

        LineSettingsParser.parse(headers).forEach { (name, value) ->
            if (!name.equals("Content-Type", ignoreCase = true)) {
                requestBuilder.header(name, value)
            }
        }

        return requestBuilder.build()
    }

    /**
     * Builds a multipart body containing configured fields and one file.
     *
     * @param boundary Multipart boundary.
     * @param bodyFields Persisted key/value multipart text fields.
     * @param filePart Multipart file part.
     *
     * @return Multipart request body bytes.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun multipartBody(boundary: String, bodyFields: String?, filePart: CustomServerFilePart): ByteArray {
        val lineBreak = "\r\n"
        val outputStream = ByteArrayOutputStream()

        LineSettingsParser.parse(bodyFields).forEach { (name, value) ->
            outputStream.writeString("--$boundary$lineBreak")
            outputStream.writeString("Content-Disposition: form-data; name=\"$name\"$lineBreak$lineBreak")
            outputStream.writeString(value)
            outputStream.writeString(lineBreak)
        }

        outputStream.writeString("--$boundary$lineBreak")
        outputStream.writeString(
            "Content-Disposition: form-data; name=\"${filePart.fieldName}\"; filename=\"${filePart.fileName}\"$lineBreak",
        )
        outputStream.writeString("Content-Type: ${filePart.contentType}$lineBreak$lineBreak")
        outputStream.write(filePart.bytes)
        outputStream.writeString(lineBreak)
        outputStream.writeString("--$boundary--$lineBreak")

        return outputStream.toByteArray()
    }

    /**
     * Adds configured query parameters to a request URL.
     *
     * @param requestUrl Base request URL.
     * @param parameters Raw line-based parameters.
     *
     * @return Request URI with parameters.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uriWithParameters(requestUrl: String, parameters: String?): URI {
        val parsedParameters = LineSettingsParser.parse(parameters)

        if (parsedParameters.isEmpty()) {
            return URI.create(requestUrl)
        }

        val separator = if (requestUrl.contains("?")) "&" else "?"
        val query = parsedParameters.joinToString("&") { (name, value) ->
            "${encode(name)}=${encode(value)}"
        }

        return URI.create("$requestUrl$separator$query")
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
     * Writes UTF-8 text to a byte array output stream.
     *
     * @param value Text to write.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun ByteArrayOutputStream.writeString(value: String) {
        write(value.toByteArray(StandardCharsets.UTF_8))
    }
}
