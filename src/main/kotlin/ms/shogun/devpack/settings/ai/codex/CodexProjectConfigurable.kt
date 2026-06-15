package ms.shogun.devpack.settings.ai.codex

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBCheckBox

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.settings.BaseProjectConfigurable
import ms.shogun.devpack.settings.ShogunDevPackProjectSettings

/**
 * Project settings page for Codex behavior.
 *
 * @param project Project owning the configurable.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodexProjectConfigurable(project: Project) : BaseProjectConfigurable(project) {
    private var additionalArgumentsTextArea: JBTextArea? = null
    private var resumeProjectSessionCheckBox: JBCheckBox? = null
    private var allowProjectWorkWithoutApprovalCheckBox: JBCheckBox? = null

    override fun getId(): String = "ms.shogun.devpack.project.settings"

    override fun getDisplayName(): String = message("settings.project.display-name")

    override fun createComponent(): JComponent {
        resumeProjectSessionCheckBox = JBCheckBox(message("settings.ai.codex.resume-project-session"))
        allowProjectWorkWithoutApprovalCheckBox = JBCheckBox(message("settings.ai.codex.allow-project-work"))
        additionalArgumentsTextArea = SettingsUi.wrappingTextArea(rows = 3)

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(requireNotNull(resumeProjectSessionCheckBox), SettingsUi.fullWidthConstraints(row = 0))
            add(SettingsUi.description(message("settings.ai.codex.resume-project-session.description")), SettingsUi.fullWidthConstraints(row = 1, topInset = 2, leftInset = 8))
            add(requireNotNull(allowProjectWorkWithoutApprovalCheckBox), SettingsUi.fullWidthConstraints(row = 2, topInset = 12))
            add(SettingsUi.description(message("settings.ai.codex.allow-project-work.description")), SettingsUi.fullWidthConstraints(row = 3, topInset = 2, leftInset = 8))
            add(SettingsUi.label(message("settings.ai.codex.additional-arguments")), SettingsUi.fullWidthConstraints(row = 4, topInset = 12))
            add(SettingsUi.verticalScrollPane(requireNotNull(additionalArgumentsTextArea)), SettingsUi.fullWidthConstraints(row = 5, topInset = 4))
            add(SettingsUi.description(message("settings.ai.codex.additional-arguments.description")), SettingsUi.fullWidthConstraints(row = 6, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackProjectSettings.getInstance(project)

        return resumeProjectSessionCheckBox?.isSelected != settings.codexResumeProjectSession
            || allowProjectWorkWithoutApprovalCheckBox?.isSelected != settings.codexAllowProjectWorkWithoutApproval
            || SettingsUi.normalizedText(additionalArgumentsTextArea) != settings.codexAdditionalArguments
    }

    override fun apply() {
        val settings = ShogunDevPackProjectSettings.getInstance(project)

        settings.codexResumeProjectSession = resumeProjectSessionCheckBox?.isSelected == true
        settings.codexAllowProjectWorkWithoutApproval = allowProjectWorkWithoutApprovalCheckBox?.isSelected == true
        settings.codexAdditionalArguments = SettingsUi.normalizedText(additionalArgumentsTextArea)
    }

    override fun reset() {
        val settings = ShogunDevPackProjectSettings.getInstance(project)

        resumeProjectSessionCheckBox?.isSelected = settings.codexResumeProjectSession
        allowProjectWorkWithoutApprovalCheckBox?.isSelected = settings.codexAllowProjectWorkWithoutApproval
        additionalArgumentsTextArea?.text = settings.codexAdditionalArguments.orEmpty()
    }

    override fun disposeUIResources() {
        additionalArgumentsTextArea = null
        resumeProjectSessionCheckBox = null
        allowProjectWorkWithoutApprovalCheckBox = null
    }
}
