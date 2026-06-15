package ms.shogun.devpack.settings.codeShare

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.codeShare.CodeShareSecrets
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.codeShare.pastebin.PastebinVisibility

/**
 * Application settings page for Pastebin code sharing.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class PastebinConfigurable : SearchableConfigurable {
    private var loadedPastebinUserKey: String? = null
    private var pastebinUserKeyLoaded: Boolean = false
    private var loadedPastebinDeveloperKey: String? = null
    private var pastebinDeveloperKeyLoaded: Boolean = false
    private var pastebinUserKeyField: JBPasswordField? = null
    private var pastebinDeveloperKeyField: JBPasswordField? = null
    private var pastebinVisibilityComboBox: ComboBox<PastebinVisibility>? = null

    override fun getId(): String = "ms.shogun.devpack.settings.code-share.pastebin"

    override fun getDisplayName(): String = message("settings.pastebin.display-name")

    override fun createComponent(): JComponent {
        pastebinDeveloperKeyField = JBPasswordField()
        pastebinUserKeyField = JBPasswordField()
        pastebinVisibilityComboBox = ComboBox(PastebinVisibility.entries.toTypedArray())

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.label(message("settings.code-share.pastebin.developer-key")), SettingsUi.fullWidthConstraints(row = 0))
            add(requireNotNull(pastebinDeveloperKeyField), SettingsUi.fullWidthConstraints(row = 1, topInset = 4))
            add(SettingsUi.description(message("settings.code-share.pastebin.developer-key.description")), SettingsUi.fullWidthConstraints(row = 2, topInset = 2))
            add(SettingsUi.label(message("settings.code-share.pastebin.user-key")), SettingsUi.fullWidthConstraints(row = 3, topInset = 12))
            add(requireNotNull(pastebinUserKeyField), SettingsUi.fullWidthConstraints(row = 4, topInset = 4))
            add(SettingsUi.description(message("settings.code-share.pastebin.user-key.description")), SettingsUi.fullWidthConstraints(row = 5, topInset = 2))
            add(SettingsUi.label(message("settings.code-share.pastebin.visibility")), SettingsUi.fullWidthConstraints(row = 6, topInset = 12))
            add(requireNotNull(pastebinVisibilityComboBox), SettingsUi.fullWidthConstraints(row = 7, topInset = 4))
            add(SettingsUi.description(message("settings.code-share.pastebin.visibility.description")), SettingsUi.fullWidthConstraints(row = 8, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackSettings.instance

        val currentPastebinDeveloperKey = SettingsUi.normalizedPassword(pastebinDeveloperKeyField)
        val currentPastebinUserKey = SettingsUi.normalizedPassword(pastebinUserKeyField)

        val pastebinDeveloperKeyModified = if (pastebinDeveloperKeyLoaded) {
            currentPastebinDeveloperKey != loadedPastebinDeveloperKey
        } else {
            currentPastebinDeveloperKey != null
        }

        val pastebinUserKeyModified = if (pastebinUserKeyLoaded) {
            currentPastebinUserKey != loadedPastebinUserKey
        } else {
            currentPastebinUserKey != null
        }

        return selectedPastebinVisibility() != PastebinVisibility.from(settings.pastebinVisibility) ||
            pastebinDeveloperKeyModified ||
            pastebinUserKeyModified
    }

    override fun apply() {
        val settings = ShogunDevPackSettings.instance

        val currentPastebinDeveloperKey = SettingsUi.normalizedPassword(pastebinDeveloperKeyField)
        val currentPastebinUserKey = SettingsUi.normalizedPassword(pastebinUserKeyField)

        settings.pastebinVisibility = selectedPastebinVisibility().name

        loadedPastebinDeveloperKey = currentPastebinDeveloperKey
        loadedPastebinUserKey = currentPastebinUserKey
        pastebinDeveloperKeyLoaded = true
        pastebinUserKeyLoaded = true

        CodeShareSecrets.setPastebinDeveloperKeyAsync(currentPastebinDeveloperKey)
        CodeShareSecrets.setPastebinUserKeyAsync(currentPastebinUserKey)
    }

    override fun reset() {
        val settings = ShogunDevPackSettings.instance

        pastebinVisibilityComboBox?.selectedItem = PastebinVisibility.from(settings.pastebinVisibility)
        loadedPastebinDeveloperKey = null
        loadedPastebinUserKey = null
        pastebinDeveloperKeyLoaded = false
        pastebinUserKeyLoaded = false
        pastebinDeveloperKeyField?.text = ""
        pastebinUserKeyField?.text = ""

        CodeShareSecrets.pastebinDeveloperKeyAsync { developerKey ->
            loadedPastebinDeveloperKey = developerKey
            pastebinDeveloperKeyLoaded = true

            pastebinDeveloperKeyField
                ?.takeIf { field ->
                    field.password.isEmpty()
                }
                ?.text = developerKey.orEmpty()
        }

        CodeShareSecrets.pastebinUserKeyAsync { userKey ->
            loadedPastebinUserKey = userKey
            pastebinUserKeyLoaded = true

            pastebinUserKeyField
                ?.takeIf { field ->
                    field.password.isEmpty()
                }
                ?.text = userKey.orEmpty()
        }
    }

    override fun disposeUIResources() {
        pastebinUserKeyField = null
        loadedPastebinUserKey = null
        pastebinUserKeyLoaded = false
        pastebinDeveloperKeyField = null
        loadedPastebinDeveloperKey = null
        pastebinVisibilityComboBox = null
        pastebinDeveloperKeyLoaded = false
    }

    /**
     * Returns the selected Pastebin visibility.
     *
     * @return Selected visibility, or unlisted when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun selectedPastebinVisibility(): PastebinVisibility =
        pastebinVisibilityComboBox?.selectedItem as? PastebinVisibility ?: PastebinVisibility.UNLISTED
}
