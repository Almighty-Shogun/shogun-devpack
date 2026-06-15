package ms.shogun.devpack.files.actions

import ms.shogun.devpack.files.BaseFixedTemplateFileAction

/**
 * Creates a fixed-name .gitkeep file.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class GitKeepFileAction : BaseFixedTemplateFileAction(
    actionTextKey = "template.kind.gitkeep",
    templateName = "Git Keep",
    fileName = ".gitkeep",
)
