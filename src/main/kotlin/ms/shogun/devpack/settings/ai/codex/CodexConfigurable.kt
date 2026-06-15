package ms.shogun.devpack.settings.ai.codex

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBCheckBox
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.settings.ui.SettingsNotice
import ms.shogun.devpack.settings.ShogunDevPackSettings

/**
 * Application settings page for the Codex AI terminal.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodexConfigurable : SearchableConfigurable {
    private var codexEnabledCheckBox: JBCheckBox? = null
    private var codexPathTextField: TextFieldWithBrowseButton? = null

    override fun getId(): String = "ms.shogun.devpack.settings.ai-terminal.codex"

    override fun getDisplayName(): String = message("settings.codex.display-name")

    override fun createComponent(): JComponent {
        codexEnabledCheckBox = JBCheckBox(message("settings.ai.codex.enabled"))
        codexPathTextField = pathTextField(
            title = message("settings.ai.codex.path.browse-title"),
            description = message("settings.ai.codex.path.browse-description")
        )

        registerEnabledStateBinding(requireNotNull(codexEnabledCheckBox), requireNotNull(codexPathTextField))

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsNotice.warning(message("settings.ai.restart-notice")), SettingsUi.fullWidthConstraints(row = 0))
            add(requireNotNull(codexEnabledCheckBox), SettingsUi.fullWidthConstraints(row = 1, topInset = 14))
            add(SettingsUi.description(message("settings.ai.codex.enabled.description")), SettingsUi.fullWidthConstraints(row = 2, topInset = 2, leftInset = 8))
            add(SettingsUi.label(message("settings.ai.codex.path")), SettingsUi.fullWidthConstraints(row = 3, topInset = 12))
            add(requireNotNull(codexPathTextField), SettingsUi.fullWidthConstraints(row = 4, topInset = 4))
            add(SettingsUi.description(message("settings.ai.codex.path.description")), SettingsUi.fullWidthConstraints(row = 5, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackSettings.instance

        return codexEnabledCheckBox?.isSelected != settings.codexEnabled
            || normalizedPath(codexPathTextField) != settings.codexPath
    }

    override fun apply() {
        val settings = ShogunDevPackSettings.instance
        val restartRequired = settings.codexEnabled != (codexEnabledCheckBox?.isSelected == true)

        settings.codexEnabled = codexEnabledCheckBox?.isSelected == true
        settings.codexPath = normalizedPath(codexPathTextField)

        if (restartRequired) {
            promptRestart()
        }
    }

    override fun reset() {
        val settings = ShogunDevPackSettings.instance

        codexEnabledCheckBox?.isSelected = settings.codexEnabled
        codexPathTextField?.text = settings.codexPath.orEmpty()

        updatePathEnabledStates()
    }

    override fun disposeUIResources() {
        codexPathTextField = null
        codexEnabledCheckBox = null
    }

    /**
     * Enables or disables a path field together with its parent checkbox.
     *
     * @param checkBox Checkbox controlling the field state.
     * @param textField Path field controlled by the checkbox.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun registerEnabledStateBinding(checkBox: JBCheckBox, textField: TextFieldWithBrowseButton) {
        checkBox.addActionListener {
            textField.isEnabled = checkBox.isSelected
        }
    }

    /**
     * Refreshes enabled states for all Codex path fields.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun updatePathEnabledStates() {
        codexPathTextField?.isEnabled = codexEnabledCheckBox?.isSelected == true
    }

    /**
     * Creates a path field with a browse button and bounded preferred width.
     *
     * @param title Browse dialog title.
     * @param description Browse dialog description.
     *
     * @return Browse-capable text field used for executable paths.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun pathTextField(title: String, description: String): TextFieldWithBrowseButton =
        TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                TextBrowseFolderListener(
                    FileChooserDescriptor(true, false, false, false, false, false)
                        .withTitle(title)
                        .withDescription(description),
                )
            )
        }

    /**
     * Converts blank path input into a nullable setting value.
     *
     * @param textField Text field containing a path value.
     *
     * @return Trimmed path, or `null` when blank.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun normalizedPath(textField: TextFieldWithBrowseButton?): String? =
        textField
            ?.text
            ?.trim()
            ?.let { path ->
                path.ifEmpty {
                    null
                }
            }

    /**
     * Asks the user whether the IDE should restart after changing tool window registration settings.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun promptRestart() {
        val result = Messages.showYesNoDialog(
            message("settings.restart.message"),
            message("settings.restart.title"),
            message("settings.restart.now"),
            message("settings.restart.later"),
            Messages.getQuestionIcon()
        )

        if (result == Messages.YES) {
            ApplicationManager.getApplication().restart()
        }
    }
}
