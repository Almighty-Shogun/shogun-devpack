package ms.shogun.devpack.settings.codeShare

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.settings.customServer.CustomServerSettingsState
import ms.shogun.devpack.settings.customServer.CustomServerSettingsTexts
import ms.shogun.devpack.settings.customServer.BaseCustomServerConfigurable

/**
 * Settings page for Code Share custom server uploads.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShareCustomServerConfigurable : BaseCustomServerConfigurable(
    id = "ms.shogun.devpack.settings.code-share.custom-server",
    texts = CustomServerSettingsTexts(
        method = message("settings.code-share.custom-server.method"),
        requestUrl = message("settings.code-share.custom-server.url"),
        requestUrlDescription = message("settings.code-share.custom-server.url.description"),
        requestSettings = message("settings.code-share.custom-server.request-settings"),
        responseSettings = message("settings.code-share.custom-server.response-settings"),
        key = message("settings.code-share.custom-server.key"),
        value = message("settings.code-share.custom-server.value"),
        requestBody = message("settings.code-share.custom-server.body"),
        requestBodyDescription = message("settings.code-share.custom-server.body.description"),
        urlParameters = message("settings.code-share.custom-server.url-parameters"),
        urlParametersDescription = message("settings.code-share.custom-server.url-parameters.description"),
        requestHeaders = message("settings.code-share.custom-server.headers"),
        requestHeadersDescription = message("settings.code-share.custom-server.headers.description"),
        fileFormName = message("settings.code-share.custom-server.file-form-name"),
        fileFormNameDescription = message("settings.code-share.custom-server.file-form-name.description"),
        urlTemplate = message("settings.code-share.custom-server.url-template"),
        urlTemplateDescription = message("settings.code-share.custom-server.url-template.description")
    ),
    readState = {
        val settings = ShogunDevPackSettings.instance

        CustomServerSettingsState(
            method = settings.codeShareCustomServerMethod,
            url = settings.codeShareCustomServerUrl,
            requestBody = settings.codeShareCustomServerRequestBody,
            urlParameters = settings.codeShareCustomServerUrlParameters,
            requestHeaders = settings.codeShareCustomServerRequestHeaders,
            fileFormName = settings.codeShareCustomServerFileFormName,
            urlTemplate = settings.codeShareCustomServerUrlTemplate
        )
    },
    writeState = { state ->
        val settings = ShogunDevPackSettings.instance

        settings.codeShareCustomServerMethod = state.method
        settings.codeShareCustomServerUrl = state.url
        settings.codeShareCustomServerRequestBody = state.requestBody
        settings.codeShareCustomServerUrlParameters = state.urlParameters
        settings.codeShareCustomServerRequestHeaders = state.requestHeaders
        settings.codeShareCustomServerFileFormName = state.fileFormName
        settings.codeShareCustomServerUrlTemplate = state.urlTemplate
    }
)
