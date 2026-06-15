package ms.shogun.devpack.settings.customServer

/**
 * Mutable settings values used by custom server configurable pages.
 *
 * @property method Persisted HTTP method.
 * @property url Request URL.
 * @property requestBody Persisted multipart text fields.
 * @property urlParameters Persisted URL parameters.
 * @property requestHeaders Persisted request headers.
 * @property fileFormName Multipart file field name.
 * @property urlTemplate Response URL template.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class CustomServerSettingsState(
    val method: String,
    val url: String?,
    val requestBody: String?,
    val urlParameters: String?,
    val requestHeaders: String?,
    val fileFormName: String,
    val urlTemplate: String
)
