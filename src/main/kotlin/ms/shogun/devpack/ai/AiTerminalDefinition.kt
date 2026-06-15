package ms.shogun.devpack.ai

import com.intellij.openapi.project.Project

import ms.shogun.devpack.settings.ShogunDevPackProjectSettings

/**
 * Describes an AI command exposed as a project-root terminal tool window.
 *
 * @property toolWindowId Registered IntelliJ tool-window id.
 * @property command Executable command started inside the terminal.
 * @property emptyTextMessageKey Message key shown before a terminal session starts.
 * @property closeSessionMessageKey Message key used for the title-bar close action.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
interface AiTerminalDefinition {
    val command: String
    val toolWindowId: String
    val emptyTextMessageKey: String
    val closeSessionMessageKey: String

    /**
     * Checks whether this AI integration is enabled in plugin settings.
     *
     * @return `true` when the integration may be shown or started.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun isEnabled(): Boolean

    /**
     * Resolves the executable command for this AI integration.
     *
     * @return Custom executable path from settings, or the default command when no path is configured.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun resolvedCommand(): String

    /**
     * Resolves startup arguments shared by fresh and resumed AI terminal sessions.
     *
     * @param projectSettings Project-level settings used for AI terminal behavior.
     *
     * @return Startup arguments appended to the executable.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun startupArguments(projectSettings: ShogunDevPackProjectSettings): List<String>

    /**
     * Resolves the command and arguments used to start this AI terminal.
     *
     * @return Executable command with optional startup arguments.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun startupCommand(project: Project): List<String>
}
