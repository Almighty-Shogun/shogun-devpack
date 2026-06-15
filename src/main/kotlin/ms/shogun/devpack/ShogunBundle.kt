package ms.shogun.devpack

import com.intellij.DynamicBundle

/**
 * Access point for localized plugin messages.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object ShogunBundle {
    private val INSTANCE = DynamicBundle(ShogunBundle::class.java, "messages.MyMessageBundle")

    /**
     * Resolves a localized message by key and optional format parameters.
     *
     * @param key Resource bundle key to resolve.
     * @param params Optional values used by `MessageFormat` placeholders.
     *
     * @return Localized message for the requested key.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun message(key: String, vararg params: Any): String = INSTANCE.getMessage(key, *params)
}
