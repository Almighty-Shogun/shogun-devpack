package ms.shogun.devpack.files.actions

import ms.shogun.devpack.files.BaseFixedTemplateFileAction

/**
 * Creates a fixed-name .gitignore file.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class GitIgnoreFileAction : BaseFixedTemplateFileAction(
    actionTextKey = "template.kind.gitignore",
    templateName = "Git Ignore",
    fileName = ".gitignore",
)
