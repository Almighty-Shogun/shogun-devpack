package ms.shogun.devpack.settings

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.ui.components.JBCheckBox
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.projectView.ProjectViewUrlSetting

/**
 * Application settings page for Shogun's DevPack.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class ShogunDevPackConfigurable : SearchableConfigurable {
    private var hideProjectPathCheckBox: JBCheckBox? = null
    private var autoHideProjectViewOnAiToggleCheckBox: JBCheckBox? = null
    private var autoHideProjectViewOnShiftTabCheckBox: JBCheckBox? = null

    override fun getId(): String = "ms.shogun.devpack.settings"

    override fun getDisplayName(): String = message("settings.display-name")

    override fun createComponent(): JComponent {
        hideProjectPathCheckBox = JBCheckBox(message("settings.project-view.hide-path"))
        autoHideProjectViewOnAiToggleCheckBox = JBCheckBox(message("settings.project-view.auto-hide-ai-toggle"))
        autoHideProjectViewOnShiftTabCheckBox = JBCheckBox(message("settings.project-view.auto-hide-shift-tab"))

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.sectionSeparator(message("settings.project-view.title")), SettingsUi.fullWidthConstraints(row = 0))
            add(requireNotNull(hideProjectPathCheckBox), SettingsUi.fullWidthConstraints(row = 1, topInset = 10, leftInset = 22))
            add(SettingsUi.description(message("settings.project-view.hide-path.description")), SettingsUi.fullWidthConstraints(row = 2, topInset = 2, leftInset = 30))
            add(requireNotNull(autoHideProjectViewOnAiToggleCheckBox), SettingsUi.fullWidthConstraints(row = 3, topInset = 10, leftInset = 22))
            add(SettingsUi.description(message("settings.project-view.auto-hide-ai-toggle.description")), SettingsUi.fullWidthConstraints(row = 4, topInset = 2, leftInset = 30))
            add(requireNotNull(autoHideProjectViewOnShiftTabCheckBox), SettingsUi.fullWidthConstraints(row = 5, topInset = 10, leftInset = 22))
            add(SettingsUi.description(message("settings.project-view.auto-hide-shift-tab.description")), SettingsUi.fullWidthConstraints(row = 6, topInset = 2, leftInset = 30))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackSettings.instance

        return hideProjectPathCheckBox?.isSelected != ProjectViewUrlSetting.isHidden()
            || autoHideProjectViewOnAiToggleCheckBox?.isSelected != settings.autoHideProjectViewOnAiToggle
            || autoHideProjectViewOnShiftTabCheckBox?.isSelected != settings.autoHideProjectViewOnShiftTab
    }

    override fun apply() {
        val hidden = hideProjectPathCheckBox?.isSelected == true
        val settings = ShogunDevPackSettings.instance

        ProjectViewUrlSetting.setHidden(hidden)
        ProjectViewUrlSetting.refreshOpenProjectViews()

        settings.autoHideProjectViewOnAiToggle = autoHideProjectViewOnAiToggleCheckBox?.isSelected == true
        settings.autoHideProjectViewOnShiftTab = autoHideProjectViewOnShiftTabCheckBox?.isSelected == true
    }

    override fun reset() {
        val settings = ShogunDevPackSettings.instance

        hideProjectPathCheckBox?.isSelected = ProjectViewUrlSetting.isHidden()
        autoHideProjectViewOnAiToggleCheckBox?.isSelected = settings.autoHideProjectViewOnAiToggle
        autoHideProjectViewOnShiftTabCheckBox?.isSelected = settings.autoHideProjectViewOnShiftTab
    }

    override fun disposeUIResources() {
        hideProjectPathCheckBox = null
        autoHideProjectViewOnAiToggleCheckBox = null
        autoHideProjectViewOnShiftTabCheckBox = null
    }
}
