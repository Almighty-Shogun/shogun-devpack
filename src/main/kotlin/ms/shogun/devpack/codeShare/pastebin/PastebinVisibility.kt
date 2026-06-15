package ms.shogun.devpack.codeShare.pastebin

import ms.shogun.devpack.ShogunBundle.message

/**
 * Pastebin visibility modes supported by the Pastebin API.
 *
 * @property apiValue Numeric value expected by Pastebin.
 * @property labelKey Resource bundle key used for display text.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
enum class PastebinVisibility(val apiValue: String, private val labelKey: String) {
    PUBLIC("0", "code-share.pastebin.visibility.public"),
    UNLISTED("1", "code-share.pastebin.visibility.unlisted"),
    PRIVATE("2", "code-share.pastebin.visibility.private");

    override fun toString(): String = message(labelKey)

    companion object {
        /**
         * Resolves a persisted visibility value with unlisted as fallback.
         *
         * @param value Persisted enum name.
         *
         * @return Matching visibility, or unlisted when unknown.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun from(value: String?): PastebinVisibility =
            entries.firstOrNull { visibility ->
                visibility.name == value
            } ?: UNLISTED
    }
}
