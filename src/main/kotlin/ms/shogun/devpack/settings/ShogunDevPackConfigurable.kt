package ms.shogun.devpack.settings

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.ui.components.JBCheckBox
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi

/**
 * Application settings page for Shogun's DevPack.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class ShogunDevPackConfigurable : SearchableConfigurable {
    private var hideProjectPathCheckBox: JBCheckBox? = null

    override fun getId(): String = "ms.shogun.devpack.settings"

    override fun getDisplayName(): String = message("settings.display-name")

    override fun createComponent(): JComponent {
        hideProjectPathCheckBox = JBCheckBox(message("settings.project-view.hide-path"))

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.sectionSeparator(message("settings.project-view.title")), SettingsUi.fullWidthConstraints(row = 0))
            add(requireNotNull(hideProjectPathCheckBox), SettingsUi.fullWidthConstraints(row = 1, topInset = 10, leftInset = 22))
            add(SettingsUi.description(message("settings.project-view.hide-path.description")), SettingsUi.fullWidthConstraints(row = 2, topInset = 2, leftInset = 30))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val settings = ShogunDevPackSettings.instance

        return hideProjectPathCheckBox?.isSelected != settings.hideProjectViewPath
    }

    override fun apply() {
        val settings = ShogunDevPackSettings.instance

        settings.hideProjectViewPath = hideProjectPathCheckBox?.isSelected == true

        refreshProjectViews()
    }

    override fun reset() {
        val settings = ShogunDevPackSettings.instance

        hideProjectPathCheckBox?.isSelected = settings.hideProjectViewPath
    }

    override fun disposeUIResources() {
        hideProjectPathCheckBox = null
    }

    /**
     * Refreshes every open Project View after changing presentation settings.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun refreshProjectViews() {
        ProjectManager.getInstance().openProjects.forEach { project ->
            ProjectView.getInstance(project).refresh()
        }
    }
}
