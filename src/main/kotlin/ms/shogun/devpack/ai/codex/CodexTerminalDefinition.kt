package ms.shogun.devpack.ai.codex

import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil

import ms.shogun.devpack.ai.AiTerminalDefinition
import ms.shogun.devpack.ai.AiTerminalShellCommand
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.settings.ShogunDevPackProjectSettings

/**
 * Codex CLI terminal integration metadata and startup command builder.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodexTerminalDefinition : AiTerminalDefinition {
    override val command = "codex"
    override val toolWindowId = "Codex"
    override val emptyTextMessageKey = "ai.codex.empty-text"
    override val closeSessionMessageKey = "ai.codex.close-session"

    override fun isEnabled(): Boolean = ShogunDevPackSettings.instance.codexEnabled

    override fun resolvedCommand(): String = ShogunDevPackSettings.instance.codexPath ?: command

    override fun startupCommand(project: Project): List<String> {
        val executable = resolvedCommand()
        val projectSettings = ShogunDevPackProjectSettings.getInstance(project)
        val startupArguments = startupArguments(projectSettings)

        if (projectSettings.codexResumeProjectSession) {
            return AiTerminalShellCommand.fallbackStartupCommand(
                resumeCommand = listOf(executable, "resume", "--last") + startupArguments,
                freshCommand = listOf(executable) + startupArguments,
            )
        }

        return listOf(executable) + startupArguments
    }

    override fun startupArguments(projectSettings: ShogunDevPackProjectSettings): List<String> {
        val arguments = mutableListOf<String>()

        if (projectSettings.codexAllowProjectWorkWithoutApproval) {
            arguments += listOf("--sandbox", "workspace-write", "--ask-for-approval", "on-request")
        }

        arguments += ParametersListUtil.parse(projectSettings.codexAdditionalArguments.orEmpty())

        return arguments
    }
}
