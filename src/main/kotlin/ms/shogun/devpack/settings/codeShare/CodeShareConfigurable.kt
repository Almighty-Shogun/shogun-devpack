package ms.shogun.devpack.settings.codeShare

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.codeShare.CodeShareUploaderType

/**
 * Application settings page for shared code upload behavior.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShareConfigurable : SearchableConfigurable {
    private var codeShareUploaderComboBox: ComboBox<CodeShareUploaderType>? = null

    override fun getId(): String = "ms.shogun.devpack.settings.code-share"

    override fun getDisplayName(): String = message("settings.code-share.display-name")

    override fun createComponent(): JComponent {
        codeShareUploaderComboBox = ComboBox(CodeShareUploaderType.entries.toTypedArray())

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsUi.description(message("settings.code-share.description")), SettingsUi.fullWidthConstraints(row = 0))
            add(SettingsUi.label(message("settings.code-share.uploader")), SettingsUi.fullWidthConstraints(row = 1, topInset = 12))
            add(requireNotNull(codeShareUploaderComboBox), SettingsUi.fullWidthConstraints(row = 2, topInset = 4))
            add(SettingsUi.description(message("settings.code-share.uploader.description")), SettingsUi.fullWidthConstraints(row = 3, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean =
        selectedCodeShareUploader() != CodeShareUploaderType.from(ShogunDevPackSettings.instance.codeShareUploader)

    override fun apply() {
        ShogunDevPackSettings.instance.codeShareUploader = selectedCodeShareUploader().name
    }

    override fun reset() {
        codeShareUploaderComboBox?.selectedItem = CodeShareUploaderType.from(
            ShogunDevPackSettings.instance.codeShareUploader
        )
    }

    override fun disposeUIResources() {
        codeShareUploaderComboBox = null
    }

    /**
     * Returns the selected code sharing uploader.
     *
     * @return Selected uploader, or GitHub Gist when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun selectedCodeShareUploader(): CodeShareUploaderType =
        codeShareUploaderComboBox?.selectedItem as? CodeShareUploaderType ?: CodeShareUploaderType.GITHUB_GIST
}
