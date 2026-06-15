package ms.shogun.devpack.settings

import com.intellij.openapi.project.Project
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.PersistentStateComponent

/**
 * Persistent project-level settings for Shogun's DevPack.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
@State(
    name = "ShogunDevPackProjectSettings",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)],
)
@Service(Service.Level.PROJECT)
class ShogunDevPackProjectSettings : PersistentStateComponent<ShogunDevPackProjectSettings.State> {
    private var settingsState = State()

    var codexResumeProjectSession: Boolean
        get() = settingsState.codexResumeProjectSession
        set(value) {
            settingsState.codexResumeProjectSession = value
        }

    var codexAllowProjectWorkWithoutApproval: Boolean
        get() = settingsState.codexAllowProjectWorkWithoutApproval
        set(value) {
            settingsState.codexAllowProjectWorkWithoutApproval = value
        }

    var codexAdditionalArguments: String?
        get() = settingsState.codexAdditionalArguments
        set(value) {
            settingsState.codexAdditionalArguments = value
        }

    var claudeResumeProjectSession: Boolean
        get() = settingsState.claudeResumeProjectSession
        set(value) {
            settingsState.claudeResumeProjectSession = value
        }

    var claudeAllowProjectWorkWithoutApproval: Boolean
        get() = settingsState.claudeAllowProjectWorkWithoutApproval
        set(value) {
            settingsState.claudeAllowProjectWorkWithoutApproval = value
        }

    var claudeAdditionalArguments: String?
        get() = settingsState.claudeAdditionalArguments
        set(value) {
            settingsState.claudeAdditionalArguments = value
        }

    override fun getState(): State = settingsState

    override fun loadState(state: State) {
        settingsState = state
    }

    /**
     * Serializable project settings values.
     *
     * @property codexResumeProjectSession Whether Codex should resume the latest session for the project directory.
     * @property codexAllowProjectWorkWithoutApproval Whether Codex should use workspace-write and on-request approvals.
     * @property codexAdditionalArguments Optional extra Codex CLI arguments for this project.
     * @property claudeResumeProjectSession Whether Claude should continue the latest session for the project directory.
     * @property claudeAllowProjectWorkWithoutApproval Whether Claude should start in accept-edits permission mode.
     * @property claudeAdditionalArguments Optional extra Claude CLI arguments for this project.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    data class State(
        var codexResumeProjectSession: Boolean = true,
        var codexAllowProjectWorkWithoutApproval: Boolean = true,
        var codexAdditionalArguments: String? = null,
        var claudeResumeProjectSession: Boolean = true,
        var claudeAllowProjectWorkWithoutApproval: Boolean = true,
        var claudeAdditionalArguments: String? = null
    )

    companion object {
        /**
         * Resolves the project settings service.
         *
         * @param project Project owning the settings.
         *
         * @return Project settings service.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun getInstance(project: Project): ShogunDevPackProjectSettings = project.service()
    }
}
