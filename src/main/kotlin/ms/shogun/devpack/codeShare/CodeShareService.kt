package ms.shogun.devpack.codeShare

import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.codeShare.uploaders.PastebinUploader
import ms.shogun.devpack.codeShare.pastebin.PastebinVisibility
import ms.shogun.devpack.codeShare.uploaders.GitHubGistUploader
import ms.shogun.devpack.codeShare.uploaders.CodeShareCustomServerUploader

/**
 * Creates and runs the configured code sharing uploader.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodeShareService {
    /**
     * Uploads code through the currently configured uploader.
     *
     * @param request Code share request prepared from the editor or Project View.
     *
     * @return Upload result containing the URL copied to the clipboard.
     *
     * @throws CodeShareException when no uploader can upload the request.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun upload(request: CodeShareRequest): CodeShareResult = uploader().upload(request)

    /**
     * Builds the currently configured uploader.
     *
     * @return Configured code sharing uploader.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploader(): CodeShareUploader {
        val settings = ShogunDevPackSettings.instance

        return when (CodeShareUploaderType.from(settings.codeShareUploader)) {
            CodeShareUploaderType.GITHUB_GIST -> GitHubGistUploader(
                token = CodeShareSecrets.githubToken(),
                publicGist = settings.githubGistPublic
            )

            CodeShareUploaderType.PASTEBIN -> PastebinUploader(
                developerKey = CodeShareSecrets.pastebinDeveloperKey(),
                userKey = CodeShareSecrets.pastebinUserKey(),
                visibility = PastebinVisibility.from(settings.pastebinVisibility)
            )

            CodeShareUploaderType.CUSTOM_SERVER -> CodeShareCustomServerUploader()
        }
    }
}
