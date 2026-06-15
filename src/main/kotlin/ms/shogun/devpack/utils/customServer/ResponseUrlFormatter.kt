package ms.shogun.devpack.utils.customServer

import com.google.gson.JsonParser
import com.google.gson.JsonElement

/**
 * Applies ShareX-style response placeholders to a copied URL.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object ResponseUrlFormatter {
    private val PLACEHOLDER_REGEX = Regex("""\{response(?:\.([A-Za-z0-9_.-]+))?}""")

    /**
     * Formats a URL template using the raw server response.
     *
     * @param template Template containing `{response}` or `{response.path.to.value}`.
     * @param responseBody Raw response body.
     *
     * @return Formatted URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun format(template: String, responseBody: String): String =
        PLACEHOLDER_REGEX.replace(template) { matchResult ->
            val path = matchResult.groupValues.getOrNull(1).orEmpty()

            if (path.isBlank()) {
                responseBody.trim()
            } else {
                jsonValue(responseBody, path).orEmpty()
            }
        }

    /**
     * Extracts a nested string-like value from a JSON response.
     *
     * @param responseBody Raw JSON response body.
     * @param path Dot-separated property path.
     *
     * @return Extracted value, or `null` when not found.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun jsonValue(responseBody: String, path: String): String? {
        val rootElement = runCatching {
            JsonParser.parseString(responseBody)
        }.getOrNull() ?: return null

        val value = path.split('.').fold(rootElement) { element, property ->
            element.property(property) ?: return null
        }

        return value.asOutputValue()
    }

    /**
     * Reads a property from a JSON object.
     *
     * @param name Property name.
     *
     * @return Property element, or `null` when unavailable.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun JsonElement.property(name: String): JsonElement? =
        takeIf { element ->
            element.isJsonObject
        }?.asJsonObject?.get(name)

    /**
     * Converts a JSON element to a copied URL template value.
     *
     * @return Primitive value without quotes, or compact JSON for object and array values.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun JsonElement.asOutputValue(): String =
        if (isJsonPrimitive) {
            asJsonPrimitive.asString
        } else {
            toString()
        }
}
