package ms.shogun.devpack.files.license

import java.awt.GridBagLayout
import java.awt.GridBagConstraints

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi

/**
 * Dialog used to collect values for generated LICENSE files.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class LicenseTemplateDialog : DialogWrapper(true) {
    private val licenseTemplates = LicenseTemplate.all()
    private val fieldPanel = JPanel(GridBagLayout())
    private val fieldByToken = mutableMapOf<String, JBTextField>()
    private val licenseComboBox = ComboBox(licenseTemplates.toTypedArray())

    init {
        title = message("license.dialog.title")
        licenseComboBox.maximumRowCount = licenseTemplates.size
        licenseComboBox.addActionListener {
            rebuildFieldPanel()
        }

        rebuildFieldPanel()
        init()
    }

    override fun createCenterPanel(): JComponent =
        JPanel(GridBagLayout()).apply {
            add(
                SettingsUi.label(message("license.dialog.type")),
                SettingsUi.cellConstraints(row = 0, column = 0, bottomInset = 8, rightInset = 8)
            )
            add(
                licenseComboBox,
                SettingsUi.cellConstraints(
                    row = 0,
                    column = 1,
                    weight = 1.0,
                    bottomInset = 8,
                    fill = GridBagConstraints.HORIZONTAL
                )
            )
            add(fieldPanel, SettingsUi.fullWidthConstraints(row = 1, topInset = 8, gridWidth = 2))
        }

    override fun doValidate(): ValidationInfo? =
        selectedLicense().placeholders()
            .firstNotNullOfOrNull { placeholder ->
                val field = fieldByToken[placeholder.token] ?: return@firstNotNullOfOrNull null

                if (field.text.trim().isEmpty()) {
                    ValidationInfo(message("license.dialog.field.required", placeholder.label), field)
                } else {
                    null
                }
            }

    /**
     * Renders the selected license with the entered values.
     *
     * @return Complete license text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun licenseText(): String =
        selectedLicense().render(
            fieldByToken.mapValues { (_, field) -> field.text.trim() }
        )

    /**
     * Rebuilds the dynamic placeholder fields for the selected license.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun rebuildFieldPanel() {
        fieldPanel.removeAll()
        fieldByToken.clear()

        val placeholders = selectedLicense().placeholders()
        if (placeholders.isEmpty()) {
            fieldPanel.add(
                SettingsUi.label(message("license.dialog.no-fields")),
                SettingsUi.fullWidthConstraints(row = 0, topInset = 8, gridWidth = 2)
            )
        } else {
            placeholders.forEachIndexed { index, placeholder ->
                val field = JBTextField(placeholder.defaultValue())
                fieldByToken[placeholder.token] = field

                fieldPanel.add(
                    SettingsUi.label(placeholder.label),
                    SettingsUi.cellConstraints(row = index, column = 0, bottomInset = 8, rightInset = 8)
                )
                fieldPanel.add(
                    field,
                    SettingsUi.cellConstraints(
                        row = index,
                        column = 1,
                        weight = 1.0,
                        bottomInset = 8,
                        fill = GridBagConstraints.HORIZONTAL
                    )
                )
            }
        }

        fieldPanel.revalidate()
        fieldPanel.repaint()
    }

    /**
     * Resolves the selected license template.
     *
     * @return Selected license template.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun selectedLicense(): LicenseTemplate =
        licenseComboBox.selectedItem as? LicenseTemplate ?: licenseTemplates.first()
}
