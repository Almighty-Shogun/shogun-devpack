package ms.shogun.devpack.settings.codeShot

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.codeShot.CodeShotOutputTarget
import ms.shogun.devpack.settings.ShogunDevPackSettings

/**
 * Application settings page for Code Shot behavior.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShotConfigurable : SearchableConfigurable {
    private var outputTargetComboBox: ComboBox<CodeShotOutputTarget>? = null

    override fun getId(): String = "ms.shogun.devpack.settings.code-shot"

    override fun getDisplayName(): String = message("settings.code-shot.display-name")

    override fun createComponent(): JComponent {
        outputTargetComboBox = ComboBox(CodeShotOutputTarget.entries.toTypedArray())

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.description(message("settings.code-shot.description")), SettingsUi.fullWidthConstraints(row = 0))
            add(SettingsUi.label(message("settings.code-shot.output-target")), SettingsUi.fullWidthConstraints(row = 1, topInset = 12))
            add(requireNotNull(outputTargetComboBox), SettingsUi.fullWidthConstraints(row = 2, topInset = 4))
            add(SettingsUi.description(message("settings.code-shot.output-target.description")), SettingsUi.fullWidthConstraints(row = 3, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean =
        selectedOutputTarget() != CodeShotOutputTarget.from(ShogunDevPackSettings.instance.codeShotOutputTarget)

    override fun apply() {
        ShogunDevPackSettings.instance.codeShotOutputTarget = selectedOutputTarget().name
    }

    override fun reset() {
        outputTargetComboBox?.selectedItem = CodeShotOutputTarget.from(
            ShogunDevPackSettings.instance.codeShotOutputTarget,
        )
    }

    override fun disposeUIResources() {
        outputTargetComboBox = null
    }

    /**
     * Returns the selected Code Shot output target.
     *
     * @return Selected output target, or clipboard when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun selectedOutputTarget(): CodeShotOutputTarget =
        outputTargetComboBox?.selectedItem as? CodeShotOutputTarget ?: CodeShotOutputTarget.CLIPBOARD
}
