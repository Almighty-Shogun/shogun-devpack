package ms.shogun.devpack.utils.customServer

/**
 * File part sent in a multipart custom server upload.
 *
 * @property fieldName Multipart form field name.
 * @property fileName File name sent to the server.
 * @property contentType MIME type sent for the file.
 * @property bytes Raw file bytes.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class CustomServerFilePart(
    val fieldName: String,
    val fileName: String,
    val contentType: String,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is CustomServerFilePart) {
            return false
        }

        return fieldName == other.fieldName &&
            fileName == other.fileName &&
            contentType == other.contentType &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = fieldName.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + bytes.contentHashCode()

        return result
    }
}
