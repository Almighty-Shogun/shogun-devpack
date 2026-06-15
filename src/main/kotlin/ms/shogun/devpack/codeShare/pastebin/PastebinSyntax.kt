package ms.shogun.devpack.codeShare.pastebin

/**
 * Maps common file extensions to Pastebin syntax names.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object PastebinSyntax {
    private val syntaxNames = mapOf(
        "cs" to "csharp",
        "css" to "css",
        "html" to "html5",
        "java" to "java",
        "js" to "javascript",
        "json" to "json",
        "kt" to "kotlin",
        "kts" to "kotlin",
        "md" to "markdown",
        "php" to "php",
        "py" to "python",
        "sh" to "bash",
        "ts" to "typescript",
        "xml" to "xml",
        "yml" to "yaml",
        "yaml" to "yaml",
    )

    /**
     * Resolves a Pastebin syntax name from a file extension.
     *
     * @param extension File extension without a dot.
     *
     * @return Pastebin syntax name, or `null` when unknown.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun fromExtension(extension: String?): String? =
        extension
            ?.lowercase()
            ?.let { syntaxName ->
                syntaxNames[syntaxName]
            }
}
