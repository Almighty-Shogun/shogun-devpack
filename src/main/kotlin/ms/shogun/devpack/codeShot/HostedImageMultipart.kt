package ms.shogun.devpack.codeShot

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

/**
 * Builds multipart upload bodies for hosted image providers.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object HostedImageMultipart {
    /**
     * Creates a multipart form body with text fields and one PNG file field.
     *
     * @param boundary Multipart boundary.
     * @param fileFieldName Multipart field name for the PNG file.
     * @param pngBytes Rendered screenshot encoded as PNG.
     * @param fields Additional text fields sent before the file.
     *
     * @return Multipart request body bytes.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun body(
        boundary: String,
        fileFieldName: String,
        pngBytes: ByteArray,
        fields: Map<String, String> = emptyMap()
    ): ByteArray {
        val lineBreak = "\r\n"
        val outputStream = ByteArrayOutputStream()

        fields.forEach { (name, value) ->
            outputStream.writeString("--$boundary$lineBreak")
            outputStream.writeString("Content-Disposition: form-data; name=\"$name\"$lineBreak$lineBreak")
            outputStream.writeString(value)
            outputStream.writeString(lineBreak)
        }

        outputStream.writeString("--$boundary$lineBreak")
        outputStream.writeString("Content-Disposition: form-data; name=\"$fileFieldName\"; filename=\"code-shot.png\"$lineBreak")
        outputStream.writeString("Content-Type: image/png$lineBreak$lineBreak")
        outputStream.write(pngBytes)
        outputStream.writeString(lineBreak)
        outputStream.writeString("--$boundary--$lineBreak")

        return outputStream.toByteArray()
    }

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
