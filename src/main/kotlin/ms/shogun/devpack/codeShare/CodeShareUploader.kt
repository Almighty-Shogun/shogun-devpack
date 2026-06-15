package ms.shogun.devpack.codeShare

/**
 * Upload target capable of publishing a code fragment or file.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
interface CodeShareUploader {
    /**
     * Uploads the provided code and returns the public page URL.
     *
     * @param request Code share request prepared from the editor or Project View.
     *
     * @return Upload result containing the URL copied to the clipboard.
     *
     * @throws CodeShareException when the uploader rejects or fails the upload.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun upload(request: CodeShareRequest): CodeShareResult
}
