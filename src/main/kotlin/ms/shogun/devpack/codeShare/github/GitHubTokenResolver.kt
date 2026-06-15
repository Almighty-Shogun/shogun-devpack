package ms.shogun.devpack.codeShare.github

import java.util.concurrent.TimeUnit

/**
 * Resolves a GitHub API token from settings, environment, or local GitHub CLI authentication.
 *
 * @param configuredToken Token configured in Shogun's DevPack settings.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class GitHubTokenResolver(private val configuredToken: String?) {
    /**
     * Returns the configured token, an environment token, or a token from `gh auth token`.
     *
     * @return GitHub token, or `null` when no local token is available.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun resolve(): String? =
        configuredToken?.takeIf { token ->
            token.isNotBlank()
        } ?: environmentToken() ?: githubCliToken()

    /**
     * Reads GitHub token environment variables inherited by the IDE process.
     *
     * @return Environment token, or `null` when no supported variable is set.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun environmentToken(): String? =
        GITHUB_TOKEN_ENVIRONMENT_VARIABLES.firstNotNullOfOrNull { variable ->
            System.getenv(variable)?.takeIf { token ->
                token.isNotBlank()
            }
        }

    /**
     * Reads the active GitHub CLI token when the user is logged in locally.
     *
     * @return GitHub CLI token, or `null` when the command is unavailable or not authenticated.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun githubCliToken(): String? =
        runCatching {
            val process = ProcessBuilder("gh", "auth", "token")
                .redirectErrorStream(true)
                .start()

            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                return null
            }

            if (process.exitValue() != 0) {
                return null
            }

            process.inputStream
                .bufferedReader()
                .readText()
                .trim()
                .takeIf { token ->
                    token.isNotBlank()
                }
        }.getOrNull()

    companion object {
        private val GITHUB_TOKEN_ENVIRONMENT_VARIABLES = listOf("GH_TOKEN", "GITHUB_TOKEN")
    }
}
