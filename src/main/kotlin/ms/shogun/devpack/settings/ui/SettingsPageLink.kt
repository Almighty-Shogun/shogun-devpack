package ms.shogun.devpack.settings.ui

import com.intellij.openapi.options.SearchableConfigurable

/**
 * Link from an overview settings page to a concrete child settings page.
 *
 * @property text Visible link text.
 * @property configurableClass Settings page class to open.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class SettingsPageLink(
    val text: String,
    val configurableClass: Class<out SearchableConfigurable>
)
