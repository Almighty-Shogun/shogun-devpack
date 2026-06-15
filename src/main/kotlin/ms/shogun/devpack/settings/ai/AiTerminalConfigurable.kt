package ms.shogun.devpack.settings.ai

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsPageLink
import ms.shogun.devpack.settings.PlaceholderConfigurable
import ms.shogun.devpack.settings.ai.codex.CodexConfigurable
import ms.shogun.devpack.settings.ai.claude.ClaudeConfigurable

/**
 * Overview settings page for AI terminal integrations.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class AiTerminalConfigurable : PlaceholderConfigurable(
    id = "ms.shogun.devpack.settings.ai-terminal",
    displayName = message("settings.ai-terminal.display-name"),
    description = message("settings.ai-terminal.description"),
    links = listOf(
        SettingsPageLink(
            text = message("settings.codex.display-name"),
            configurableClass = CodexConfigurable::class.java,
        ),
        SettingsPageLink(
            text = message("settings.claude.display-name"),
            configurableClass = ClaudeConfigurable::class.java,
        ),
    )
)
