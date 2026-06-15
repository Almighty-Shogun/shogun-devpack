package ms.shogun.devpack.ai.claude

import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil

import ms.shogun.devpack.ai.AiTerminalDefinition
import ms.shogun.devpack.ai.AiTerminalShellCommand
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.settings.ShogunDevPackProjectSettings

/**
 * Claude Code CLI terminal integration metadata and startup command builder.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object ClaudeTerminalDefinition : AiTerminalDefinition {
    override val command = "claude"
    override val toolWindowId = "Claude"
    override val emptyTextMessageKey = "ai.claude.empty-text"
    override val closeSessionMessageKey = "ai.claude.close-session"

    override fun isEnabled(): Boolean = ShogunDevPackSettings.instance.claudeEnabled

    override fun resolvedCommand(): String = ShogunDevPackSettings.instance.claudePath ?: command

    override fun startupCommand(project: Project): List<String> {
        val executable = resolvedCommand()
        val projectSettings = ShogunDevPackProjectSettings.getInstance(project)
        val startupArguments = startupArguments(projectSettings)

        if (projectSettings.claudeResumeProjectSession) {
            return AiTerminalShellCommand.fallbackStartupCommand(
                resumeCommand = listOf(executable, "--continue") + startupArguments,
                freshCommand = listOf(executable) + startupArguments,
            )
        }

        return listOf(executable) + startupArguments
    }

    override fun startupArguments(projectSettings: ShogunDevPackProjectSettings): List<String> {
        val arguments = mutableListOf<String>()

        if (projectSettings.claudeAllowProjectWorkWithoutApproval) {
            arguments += listOf("--permission-mode", "acceptEdits")
        }

        arguments += ParametersListUtil.parse(projectSettings.claudeAdditionalArguments.orEmpty())

        return arguments
    }
}
