package ms.shogun.devpack.files

import javax.swing.Icon

/**
 * Describes one selectable template option in a new-file dialog.
 *
 * @param label Visible label shown in the template chooser.
 * @param icon Icon shown beside the template option.
 * @param templateName Internal IntelliJ template name.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
data class TemplateKind(val label: String, val icon: Icon, val templateName: String)
