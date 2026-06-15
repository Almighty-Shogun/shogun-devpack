package ms.shogun.devpack.ai

import com.intellij.openapi.util.SystemInfo

/**
 * Shell command helpers shared by AI terminal definitions.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object AiTerminalShellCommand {
    /**
     * Builds a shell command that falls back to a fresh session when resuming fails.
     *
     * @param resumeCommand Command used to resume the latest project session.
     * @param freshCommand Command used to start a fresh project session.
     *
     * @return Shell command with platform-specific fallback syntax.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun fallbackStartupCommand(resumeCommand: List<String>, freshCommand: List<String>): List<String> {
        val fallbackCommand = "${shellCommand(resumeCommand)} || ${shellCommand(freshCommand)}"

        return if (SystemInfo.isWindows) {
            listOf("cmd.exe", "/c", fallbackCommand)
        } else {
            listOf("/bin/sh", "-lc", fallbackCommand)
        }
    }

    /**
     * Escapes command parts for the shell used by the fallback launcher.
     *
     * @param command Command parts to escape.
     *
     * @return Shell-safe command string.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun shellCommand(command: List<String>): String =
        command.joinToString(" ") { argument ->
            if (SystemInfo.isWindows) {
                windowsShellArgument(argument)
            } else {
                unixShellArgument(argument)
            }
        }

    /**
     * Escapes one argument for a POSIX shell.
     *
     * @param argument Argument to escape.
     *
     * @return Escaped shell argument.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun unixShellArgument(argument: String): String = "'${argument.replace("'", "'\"'\"'")}'"

    /**
     * Escapes one argument for Windows `cmd.exe`.
     *
     * @param argument Argument to escape.
     *
     * @return Escaped shell argument.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun windowsShellArgument(argument: String): String = "\"${argument.replace("\"", "\\\"")}\""
}
