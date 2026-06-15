package ms.shogun.devpack.files.actions

import java.util.Properties

import com.intellij.openapi.project.Project

import ms.shogun.devpack.files.BaseFixedTemplateFileAction
import ms.shogun.devpack.files.license.LicenseTemplateDialog

/**
 * Creates a fixed-name LICENSE file from the selected license template.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class LicenseFileAction : BaseFixedTemplateFileAction(
    actionTextKey = "template.kind.license",
    templateName = "LICENSE",
    fileName = "LICENSE",
) {
    override fun customizeProperties(project: Project, properties: Properties): Properties? {
        val dialog = LicenseTemplateDialog()
        if (!dialog.showAndGet()) {
            return null
        }

        properties.setProperty("LICENSE_TEXT", dialog.licenseText())

        return properties
    }
}
