package ms.shogun.devpack.codeShare

import ms.shogun.devpack.ShogunBundle.message

/**
 * Supported code sharing uploaders.
 *
 * @property labelKey Resource bundle key used for display text.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
enum class CodeShareUploaderType(private val labelKey: String) {
    PASTEBIN("code-share.uploader.pastebin"),
    GITHUB_GIST("code-share.uploader.github-gist"),
    CUSTOM_SERVER("code-share.uploader.custom-server");

    override fun toString(): String = message(labelKey)

    companion object {
        /**
         * Resolves a persisted uploader value with GitHub Gist as fallback.
         *
         * @param value Persisted enum name.
         *
         * @return Matching uploader type, or GitHub Gist when unknown.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun from(value: String?): CodeShareUploaderType =
            entries.firstOrNull { uploaderType ->
                uploaderType.name == value
            } ?: GITHUB_GIST
    }
}
