package ms.shogun.devpack.codeShare

/**
 * Code content prepared for upload.
 *
 * @property fileName File name shown by the sharing uploader.
 * @property content Code content to upload.
 * @property syntaxName Optional syntax name derived from the file extension.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class CodeShareRequest(val fileName: String, val content: String, val syntaxName: String?)
