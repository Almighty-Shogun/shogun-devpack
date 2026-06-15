package ms.shogun.devpack.files.actions

import ms.shogun.devpack.PluginIcons
import ms.shogun.devpack.files.TemplateKind
import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.files.BaseTemplateFileAction

/**
 * Creates JSON files from the internal JSON template.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class JsonFileAction : BaseTemplateFileAction(
    actionName = message("file.action.json.name"),
    dialogTitle = message("file.action.json.dialog"),
) {
    override val templateKinds = listOf(
        TemplateKind(message("template.kind.json"), PluginIcons.json, "JSON"),
    )
}
