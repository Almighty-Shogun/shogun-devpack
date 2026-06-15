package ms.shogun.devpack.settings.codeShot

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.settings.customServer.CustomServerSettingsState
import ms.shogun.devpack.settings.customServer.CustomServerSettingsTexts
import ms.shogun.devpack.settings.customServer.BaseCustomServerConfigurable

/**
 * Settings page for Code Shot custom server uploads.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShotCustomServerConfigurable : BaseCustomServerConfigurable(
    id = "ms.shogun.devpack.settings.code-shot.custom-server",
    texts = CustomServerSettingsTexts(
        method = message("settings.code-shot.custom-server.method"),
        requestUrl = message("settings.code-shot.custom-server.url"),
        requestUrlDescription = message("settings.code-shot.custom-server.url.description"),
        requestSettings = message("settings.code-shot.custom-server.request-settings"),
        responseSettings = message("settings.code-shot.custom-server.response-settings"),
        key = message("settings.code-shot.custom-server.key"),
        value = message("settings.code-shot.custom-server.value"),
        requestBody = message("settings.code-shot.custom-server.body"),
        requestBodyDescription = message("settings.code-shot.custom-server.body.description"),
        urlParameters = message("settings.code-shot.custom-server.url-parameters"),
        urlParametersDescription = message("settings.code-shot.custom-server.url-parameters.description"),
        requestHeaders = message("settings.code-shot.custom-server.headers"),
        requestHeadersDescription = message("settings.code-shot.custom-server.headers.description"),
        fileFormName = message("settings.code-shot.custom-server.file-form-name"),
        fileFormNameDescription = message("settings.code-shot.custom-server.file-form-name.description"),
        urlTemplate = message("settings.code-shot.custom-server.url-template"),
        urlTemplateDescription = message("settings.code-shot.custom-server.url-template.description")
    ),
    readState = {
        val settings = ShogunDevPackSettings.instance

        CustomServerSettingsState(
            method = settings.codeShotCustomServerMethod,
            url = settings.codeShotCustomServerUrl,
            requestBody = settings.codeShotCustomServerRequestBody,
            urlParameters = settings.codeShotCustomServerUrlParameters,
            requestHeaders = settings.codeShotCustomServerRequestHeaders,
            fileFormName = settings.codeShotCustomServerFileFormName,
            urlTemplate = settings.codeShotCustomServerImageUrlTemplate
        )
    },
    writeState = { state ->
        val settings = ShogunDevPackSettings.instance

        settings.codeShotCustomServerMethod = state.method
        settings.codeShotCustomServerUrl = state.url
        settings.codeShotCustomServerRequestBody = state.requestBody
        settings.codeShotCustomServerUrlParameters = state.urlParameters
        settings.codeShotCustomServerRequestHeaders = state.requestHeaders
        settings.codeShotCustomServerFileFormName = state.fileFormName
        settings.codeShotCustomServerImageUrlTemplate = state.urlTemplate
    }
)
