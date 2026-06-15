package ms.shogun.devpack.codeShot

import ms.shogun.devpack.ShogunBundle.message

/**
 * Output target used after rendering a Code Shot image.
 *
 * @property labelKey Resource bundle key used for display text.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
enum class CodeShotOutputTarget(private val labelKey: String) {
    CLIPBOARD("code-shot.output.clipboard"),
    FREEIMAGE_HOST("code-shot.output.freeimage-host"),
    IMGBB("code-shot.output.imgbb"),
    CUSTOM_SERVER("code-shot.output.custom-server");

    override fun toString(): String = message(labelKey)

    companion object {
        /**
         * Resolves a persisted output target value with clipboard as fallback.
         *
         * @param value Persisted enum name.
         *
         * @return Matching output target, or clipboard when unknown.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun from(value: String?): CodeShotOutputTarget =
            entries.firstOrNull { target ->
                target.name == value
            } ?: CLIPBOARD
    }
}
