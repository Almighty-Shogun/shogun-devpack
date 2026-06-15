package ms.shogun.devpack.utils.customServer

/**
 * HTTP methods supported by custom server uploads.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
enum class HttpMethod {
    POST,
    PUT,
    PATCH;

    override fun toString(): String = name

    companion object {
        /**
         * Resolves a persisted HTTP method with POST as fallback.
         *
         * @param value Persisted method name.
         *
         * @return Matching HTTP method, or POST when unknown.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun from(value: String?): HttpMethod =
            entries.firstOrNull { method ->
                method.name == value
            } ?: POST
    }
}
