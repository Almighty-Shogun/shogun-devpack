package ms.shogun.devpack.files.actions

import ms.shogun.devpack.files.BaseFixedTemplateFileAction

/**
 * Creates a fixed-name CODEOWNERS file.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeownersFileAction : BaseFixedTemplateFileAction(
    actionTextKey = "template.kind.codeowners",
    templateName = "CODEOWNERS",
    fileName = "CODEOWNERS",
)
