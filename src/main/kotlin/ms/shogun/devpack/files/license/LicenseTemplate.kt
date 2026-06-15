package ms.shogun.devpack.files.license

/**
 * License template loaded from bundled GitHub license resources.
 *
 * @property id Stable GitHub license identifier.
 * @property title Visible license title.
 * @property spdxId SPDX identifier.
 * @property text Raw license text with supported placeholders.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class LicenseTemplate(val id: String, val title: String, val spdxId: String, val text: String) {
    /**
     * Finds supported placeholders used by this license template.
     *
     * @return Placeholders in the order they appear in the template.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun placeholders(): List<LicensePlaceholder> =
        LicensePlaceholder.entries
            .mapNotNull { placeholder ->
                text.indexOf(placeholder.markedToken())
                    .takeIf { index ->
                        index >= 0
                    }
                    ?.let { index ->
                        index to placeholder
                    }
            }
            .sortedBy { (index, _) ->
                index
            }
            .map { (_, placeholder) ->
                placeholder
            }

    /**
     * Renders the license text with user-entered placeholder values.
     *
     * @param values Values keyed by placeholder token.
     *
     * @return Rendered license text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun render(values: Map<String, String>): String =
        LicensePlaceholder.entries.fold(text) { renderedText, placeholder ->
            renderedText.replace(
                placeholder.markedToken(),
                values[placeholder.token].orEmpty(),
            )
        }

    override fun toString(): String = "$title ($spdxId)"

    companion object {
        private const val LICENSE_RESOURCE_DIRECTORY = "/licenses/github"

        /**
         * Loads all bundled license templates.
         *
         * @return License templates in the bundled GitHub picker order.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun all(): List<LicenseTemplate> =
            requireNotNull(LicenseTemplate::class.java.getResourceAsStream("$LICENSE_RESOURCE_DIRECTORY/index.tsv")) {
                "Missing bundled license index."
            }.bufferedReader().useLines { lines ->
                lines
                    .filter(String::isNotBlank)
                    .map(::fromIndexLine)
                    .toList()
            }

        /**
         * Loads one license template from an index line.
         *
         * @param line Tab-separated index line.
         *
         * @return Loaded license template.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        private fun fromIndexLine(line: String): LicenseTemplate {
            val parts = line.split('\t')

            val id = parts[0]

            val title = parts.getOrElse(1) { id }
            val spdxId = parts.getOrElse(2) { id }

            val text = requireNotNull(LicenseTemplate::class.java.getResource("$LICENSE_RESOURCE_DIRECTORY/$id.txt")) {
                "Missing bundled license text for $id."
            }.readText()

            return LicenseTemplate(id, title, spdxId, text)
        }
    }
}
