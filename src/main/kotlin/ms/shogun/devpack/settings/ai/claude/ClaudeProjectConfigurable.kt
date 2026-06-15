package ms.shogun.devpack.settings.ai.claude

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
 * Project settings page for Claude behavior.
 *
 * @param project Project owning the configurable.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class ClaudeProjectConfigurable(project: Project) : BaseProjectConfigurable(project) {
    private var additionalArgumentsTextArea: JBTextArea? = null
    private var resumeProjectSessionCheckBox: JBCheckBox? = null
    private var allowProjectWorkWithoutApprovalCheckBox: JBCheckBox? = null

    override fun getId(): String = "ms.shogun.devpack.project.settings.claude"

    override fun getDisplayName(): String = message("settings.project.display-name")

    override fun createComponent(): JComponent {
        resumeProjectSessionCheckBox = JBCheckBox(message("settings.ai.claude.resume-project-session"))
        allowProjectWorkWithoutApprovalCheckBox = JBCheckBox(message("settings.ai.claude.allow-project-work"))
        additionalArgumentsTextArea = SettingsUi.wrappingTextArea(rows = 3)

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(requireNotNull(resumeProjectSessionCheckBox), SettingsUi.fullWidthConstraints(row = 0))
            add(SettingsUi.description(message("settings.ai.claude.resume-project-session.description")), SettingsUi.fullWidthConstraints(row = 1, topInset = 2, leftInset = 8))
            add(requireNotNull(allowProjectWorkWithoutApprovalCheckBox), SettingsUi.fullWidthConstraints(row = 2, topInset = 12))
            add(SettingsUi.description(message("settings.ai.claude.allow-project-work.description")), SettingsUi.fullWidthConstraints(row = 3, topInset = 2, leftInset = 8))
            add(SettingsUi.label(message("settings.ai.claude.additional-arguments")), SettingsUi.fullWidthConstraints(row = 4, topInset = 12))
            add(SettingsUi.verticalScrollPane(requireNotNull(additionalArgumentsTextArea)), SettingsUi.fullWidthConstraints(row = 5, topInset = 4))
            add(SettingsUi.description(message("settings.ai.claude.additional-arguments.description")), SettingsUi.fullWidthConstraints(row = 6, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackProjectSettings.getInstance(project)

        return resumeProjectSessionCheckBox?.isSelected != settings.claudeResumeProjectSession
            || allowProjectWorkWithoutApprovalCheckBox?.isSelected != settings.claudeAllowProjectWorkWithoutApproval
            || SettingsUi.normalizedText(additionalArgumentsTextArea) != settings.claudeAdditionalArguments
    }

    override fun apply() {
        val settings = ShogunDevPackProjectSettings.getInstance(project)

        settings.claudeResumeProjectSession = resumeProjectSessionCheckBox?.isSelected == true
        settings.claudeAllowProjectWorkWithoutApproval = allowProjectWorkWithoutApprovalCheckBox?.isSelected == true
        settings.claudeAdditionalArguments = SettingsUi.normalizedText(additionalArgumentsTextArea)
    }

    override fun reset() {
        val settings = ShogunDevPackProjectSettings.getInstance(project)

        resumeProjectSessionCheckBox?.isSelected = settings.claudeResumeProjectSession
        allowProjectWorkWithoutApprovalCheckBox?.isSelected = settings.claudeAllowProjectWorkWithoutApproval
        additionalArgumentsTextArea?.text = settings.claudeAdditionalArguments.orEmpty()
    }

    override fun disposeUIResources() {
        additionalArgumentsTextArea = null
        resumeProjectSessionCheckBox = null
        allowProjectWorkWithoutApprovalCheckBox = null
    }
}
