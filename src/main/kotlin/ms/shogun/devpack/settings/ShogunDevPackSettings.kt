package ms.shogun.devpack.settings

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.components.PersistentStateComponent

/**
 * Persistent application settings for Shogun's DevPack.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
@State(
    name = "ShogunDevPackSettings",
    storages = [Storage("shogun-devpack.xml")],
)
class ShogunDevPackSettings : PersistentStateComponent<ShogunDevPackSettings.State> {
    private var settingsState = State()

    var hideProjectViewPath: Boolean
        get() = settingsState.hideProjectViewPath
        set(value) {
            settingsState.hideProjectViewPath = value
        }

    var codexEnabled: Boolean
        get() = settingsState.codexEnabled
        set(value) {
            settingsState.codexEnabled = value
        }

    var codexPath: String?
        get() = settingsState.codexPath
        set(value) {
            settingsState.codexPath = value
        }

    var claudeEnabled: Boolean
        get() = settingsState.claudeEnabled
        set(value) {
            settingsState.claudeEnabled = value
        }

    var claudePath: String?
        get() = settingsState.claudePath
        set(value) {
            settingsState.claudePath = value
        }

    var codeShareUploader: String
        get() = settingsState.codeShareUploader
        set(value) {
            settingsState.codeShareUploader = value
        }

    var githubGistPublic: Boolean
        get() = settingsState.githubGistPublic
        set(value) {
            settingsState.githubGistPublic = value
        }

    var pastebinVisibility: String
        get() = settingsState.pastebinVisibility
        set(value) {
            settingsState.pastebinVisibility = value
        }

    var codeShotOutputTarget: String
        get() = settingsState.codeShotOutputTarget
        set(value) {
            settingsState.codeShotOutputTarget = value
        }

    var codeShareCustomServerMethod: String
        get() = settingsState.codeShareCustomServerMethod
        set(value) {
            settingsState.codeShareCustomServerMethod = value
        }

    var codeShareCustomServerUrl: String?
        get() = settingsState.codeShareCustomServerUrl
        set(value) {
            settingsState.codeShareCustomServerUrl = value
        }

    var codeShareCustomServerRequestBody: String?
        get() = settingsState.codeShareCustomServerRequestBody
        set(value) {
            settingsState.codeShareCustomServerRequestBody = value
        }

    var codeShareCustomServerUrlParameters: String?
        get() = settingsState.codeShareCustomServerUrlParameters
        set(value) {
            settingsState.codeShareCustomServerUrlParameters = value
        }

    var codeShareCustomServerRequestHeaders: String?
        get() = settingsState.codeShareCustomServerRequestHeaders
        set(value) {
            settingsState.codeShareCustomServerRequestHeaders = value
        }

    var codeShareCustomServerFileFormName: String
        get() = settingsState.codeShareCustomServerFileFormName
        set(value) {
            settingsState.codeShareCustomServerFileFormName = value
        }

    var codeShareCustomServerUrlTemplate: String
        get() = settingsState.codeShareCustomServerUrlTemplate
        set(value) {
            settingsState.codeShareCustomServerUrlTemplate = value
        }

    var codeShotCustomServerMethod: String
        get() = settingsState.codeShotCustomServerMethod
        set(value) {
            settingsState.codeShotCustomServerMethod = value
        }

    var codeShotCustomServerUrl: String?
        get() = settingsState.codeShotCustomServerUrl
        set(value) {
            settingsState.codeShotCustomServerUrl = value
        }

    var codeShotCustomServerRequestBody: String?
        get() = settingsState.codeShotCustomServerRequestBody
        set(value) {
            settingsState.codeShotCustomServerRequestBody = value
        }

    var codeShotCustomServerUrlParameters: String?
        get() = settingsState.codeShotCustomServerUrlParameters
        set(value) {
            settingsState.codeShotCustomServerUrlParameters = value
        }

    var codeShotCustomServerRequestHeaders: String?
        get() = settingsState.codeShotCustomServerRequestHeaders
        set(value) {
            settingsState.codeShotCustomServerRequestHeaders = value
        }

    var codeShotCustomServerFileFormName: String
        get() = settingsState.codeShotCustomServerFileFormName
        set(value) {
            settingsState.codeShotCustomServerFileFormName = value
        }

    var codeShotCustomServerImageUrlTemplate: String
        get() = settingsState.codeShotCustomServerImageUrlTemplate
        set(value) {
            settingsState.codeShotCustomServerImageUrlTemplate = value
        }

    override fun getState(): State = settingsState

    override fun loadState(state: State) {
        settingsState = state
    }

    /**
     * Serializable settings values.
     *
     * @property hideProjectViewPath Whether the Project View should hide root path text.
     * @property codexEnabled Whether the Codex terminal integration is enabled.
     * @property codexPath Optional custom Codex executable path.
     * @property claudeEnabled Whether the Claude terminal integration is enabled.
     * @property claudePath Optional custom Claude executable path.
     * @property codeShareUploader Selected code sharing uploader.
     * @property githubGistPublic Whether GitHub Gist uploads should be public.
     * @property pastebinVisibility Selected Pastebin visibility.
     * @property codeShareCustomServerMethod HTTP method used for Code Share custom server uploads.
     * @property codeShareCustomServerUrl Upload endpoint used for Code Share custom server uploads.
     * @property codeShareCustomServerRequestBody Additional multipart form fields.
     * @property codeShareCustomServerUrlParameters Additional URL parameters.
     * @property codeShareCustomServerRequestHeaders Additional request headers.
     * @property codeShareCustomServerFileFormName Multipart file form field name.
     * @property codeShareCustomServerUrlTemplate Template used to build the copied code URL.
     * @property codeShotOutputTarget Selected Code Shot output target.
     * @property codeShotCustomServerMethod HTTP method used for Code Shot custom server uploads.
     * @property codeShotCustomServerUrl Upload endpoint used for Code Shot custom server uploads.
     * @property codeShotCustomServerRequestBody Additional multipart form fields.
     * @property codeShotCustomServerUrlParameters Additional URL parameters.
     * @property codeShotCustomServerRequestHeaders Additional request headers.
     * @property codeShotCustomServerFileFormName Multipart file form field name.
     * @property codeShotCustomServerImageUrlTemplate Template used to build the copied image URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    data class State(
        var hideProjectViewPath: Boolean = false,
        var codexEnabled: Boolean = true,
        var codexPath: String? = null,
        var claudeEnabled: Boolean = true,
        var claudePath: String? = null,
        var codeShareUploader: String = "GITHUB_GIST",
        var githubGistPublic: Boolean = false,
        var pastebinVisibility: String = "UNLISTED",
        var codeShareCustomServerMethod: String = "POST",
        var codeShareCustomServerUrl: String? = null,
        var codeShareCustomServerRequestBody: String? = null,
        var codeShareCustomServerUrlParameters: String? = null,
        var codeShareCustomServerRequestHeaders: String? = null,
        var codeShareCustomServerFileFormName: String = "file",
        var codeShareCustomServerUrlTemplate: String = "{response}",
        var codeShotOutputTarget: String = "CLIPBOARD",
        var codeShotCustomServerMethod: String = "POST",
        var codeShotCustomServerUrl: String? = null,
        var codeShotCustomServerRequestBody: String? = null,
        var codeShotCustomServerUrlParameters: String? = null,
        var codeShotCustomServerRequestHeaders: String? = null,
        var codeShotCustomServerFileFormName: String = "file",
        var codeShotCustomServerImageUrlTemplate: String = "{response}"
    )

    companion object {
        val instance: ShogunDevPackSettings
            get() = service()
    }
}
