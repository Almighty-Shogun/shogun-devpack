package ms.shogun.devpack.settings.codeShare

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.settings.ui.SettingsNotice
import ms.shogun.devpack.codeShare.CodeShareSecrets
import ms.shogun.devpack.settings.ShogunDevPackSettings

/**
 * Application settings page for GitHub Gist code sharing.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class GitHubGistConfigurable : SearchableConfigurable {
    private var loadedGithubToken: String? = null
    private var githubTokenLoaded: Boolean = false
    private var githubPublicCheckBox: JBCheckBox? = null
    private var githubTokenField: JBPasswordField? = null

    override fun getId(): String = "ms.shogun.devpack.settings.code-share.github-gist"

    override fun getDisplayName(): String = message("settings.github-gist.display-name")

    override fun createComponent(): JComponent {
        githubTokenField = JBPasswordField()
        githubPublicCheckBox = JBCheckBox(message("settings.code-share.github.public"))

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.label(message("settings.code-share.github.token")), SettingsUi.fullWidthConstraints(row = 0))
            add(requireNotNull(githubTokenField), SettingsUi.fullWidthConstraints(row = 1, topInset = 4))
            add(SettingsUi.description(message("settings.code-share.github.token.description")), SettingsUi.fullWidthConstraints(row = 2, topInset = 2))
            add(SettingsNotice.info(message("settings.code-share.github.token.notice")), SettingsUi.fullWidthConstraints(row = 3, topInset = 8))
            add(requireNotNull(githubPublicCheckBox), SettingsUi.fullWidthConstraints(row = 4, topInset = 14))
            add(SettingsUi.description(message("settings.code-share.github.public.description")), SettingsUi.fullWidthConstraints(row = 5, topInset = 2, leftInset = 8))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackSettings.instance
        val currentGithubToken = SettingsUi.normalizedPassword(githubTokenField)

        val githubTokenModified = if (githubTokenLoaded) {
            currentGithubToken != loadedGithubToken
        } else {
            currentGithubToken != null
        }

        return githubPublicCheckBox?.isSelected != settings.githubGistPublic ||
            githubTokenModified
    }

    override fun apply() {
        val settings = ShogunDevPackSettings.instance
        val currentGithubToken = SettingsUi.normalizedPassword(githubTokenField)

        settings.githubGistPublic = githubPublicCheckBox?.isSelected == true

        githubTokenLoaded = true
        loadedGithubToken = currentGithubToken

        CodeShareSecrets.setGithubTokenAsync(currentGithubToken)
    }

    override fun reset() {
        val settings = ShogunDevPackSettings.instance

        githubPublicCheckBox?.isSelected = settings.githubGistPublic

        loadedGithubToken = null
        githubTokenLoaded = false
        githubTokenField?.text = ""

        CodeShareSecrets.githubTokenAsync { token ->
            loadedGithubToken = token
            githubTokenLoaded = true

            githubTokenField
                ?.takeIf { field ->
                    field.password.isEmpty()
                }
                ?.text = token.orEmpty()
        }
    }

    override fun disposeUIResources() {
        githubTokenField = null
        loadedGithubToken = null
        githubTokenLoaded = false
        githubPublicCheckBox = null
    }
}
