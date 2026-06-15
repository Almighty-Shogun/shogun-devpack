package ms.shogun.devpack.files.actions

import ms.shogun.devpack.PluginIcons
import ms.shogun.devpack.files.TemplateKind
import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.files.BaseTemplateFileAction

/**
 * Creates Markdown files from the internal Markdown template.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class MarkdownFileAction : BaseTemplateFileAction(
    actionName = message("file.action.markdown.name"),
    dialogTitle = message("file.action.markdown.dialog"),
) {
    override val templateKinds = listOf(
        TemplateKind(message("template.kind.markdown"), PluginIcons.markdown, "Markdown"),
    )
}
