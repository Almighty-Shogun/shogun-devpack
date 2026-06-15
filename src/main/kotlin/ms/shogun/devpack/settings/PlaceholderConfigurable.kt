package ms.shogun.devpack.settings

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.ui.components.ActionLink
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.settings.ui.SettingsPageLink

/**
 * Simple read-only configurable used for feature pages that are not implemented yet.
 *
 * @param id Stable settings identifier.
 * @param displayName Visible settings tree name.
 * @param description Body text shown under the title.
 * @param links Optional links to child settings pages.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class PlaceholderConfigurable(
    private val id: String,
    private val displayName: String,
    private val description: String,
    private val links: List<SettingsPageLink> = emptyList()
) : SearchableConfigurable {
    override fun getId(): String = id

    override fun getDisplayName(): String = displayName

    override fun createComponent(): JComponent =
        JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.description(description), SettingsUi.fullWidthConstraints(row = 0))

            links.forEachIndexed { index, link ->
                add(settingsPageLink(link), SettingsUi.fullWidthConstraints(row = index + 1, topInset = if (index == 0) 12 else 4))
            }

            add(JPanel(), SettingsUi.fillerConstraints())
        }

    override fun isModified(): Boolean = false

    override fun apply() {}

    override fun reset() {}

    override fun disposeUIResources() {}

    /**
     * Creates a link that opens a settings page.
     *
     * @param link Link target metadata.
     *
     * @return Action link component.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun settingsPageLink(link: SettingsPageLink): ActionLink =
        ActionLink(link.text) {
            ShowSettingsUtil.getInstance().showSettingsDialog(null, link.configurableClass)
        }
}
