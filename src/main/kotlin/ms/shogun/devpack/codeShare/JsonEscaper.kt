package ms.shogun.devpack.codeShare

/**
 * Small JSON string escaper used for uploader payloads without extra dependencies.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object JsonEscaper {
    /**
     * Escapes a Kotlin string as a JSON string literal body.
     *
     * @param value Raw string value.
     *
     * @return Escaped JSON string content without surrounding quotes.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun escape(value: String): String =
        buildString {
            value.forEach { character ->
                when (character) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\b' -> append("\\b")
                    '\u000C' -> append("\\f")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(character)
                }
            }
        }
}
