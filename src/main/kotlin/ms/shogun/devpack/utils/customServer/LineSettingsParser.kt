package ms.shogun.devpack.utils.customServer

import ms.shogun.devpack.settings.ui.KeyValueSettingsPanel

/**
 * Parses line-based settings in `name=value` or `name: value` format.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object LineSettingsParser {
    /**
     * Parses configured lines into key/value pairs.
     *
     * @param text Raw multi-line setting text.
     *
     * @return Parsed key/value pairs.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun parse(text: String?): List<Pair<String, String>> = KeyValueSettingsPanel.parse(text)
}
