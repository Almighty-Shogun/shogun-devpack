package ms.shogun.devpack.settings.customServer

/**
 * Localized labels and descriptions used by a custom server settings page.
 *
 * @property method HTTP method label.
 * @property requestUrl Request URL label.
 * @property requestUrlDescription Request URL description.
 * @property requestSettings Request settings section title.
 * @property responseSettings Response settings section title.
 * @property key Key column label.
 * @property value Value column label.
 * @property requestBody Request body tab label.
 * @property requestBodyDescription Request body tab description.
 * @property urlParameters URL parameters tab label.
 * @property urlParametersDescription URL parameters tab description.
 * @property requestHeaders Request headers tab label.
 * @property requestHeadersDescription Request headers tab description.
 * @property fileFormName File form name label.
 * @property fileFormNameDescription File form name description.
 * @property urlTemplate URL template label.
 * @property urlTemplateDescription URL template description.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class CustomServerSettingsTexts(
    val method: String,
    val requestUrl: String,
    val requestUrlDescription: String,
    val requestSettings: String,
    val responseSettings: String,
    val key: String,
    val value: String,
    val requestBody: String,
    val requestBodyDescription: String,
    val urlParameters: String,
    val urlParametersDescription: String,
    val requestHeaders: String,
    val requestHeadersDescription: String,
    val fileFormName: String,
    val fileFormNameDescription: String,
    val urlTemplate: String,
    val urlTemplateDescription: String
)
