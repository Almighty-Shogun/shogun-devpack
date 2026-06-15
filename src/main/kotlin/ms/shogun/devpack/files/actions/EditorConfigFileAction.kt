package ms.shogun.devpack.files.actions

import ms.shogun.devpack.files.BaseFixedTemplateFileAction

/**
 * Creates a fixed-name .editorconfig file.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class EditorConfigFileAction : BaseFixedTemplateFileAction(
    actionTextKey = "template.kind.editorconfig",
    templateName = "EditorConfig",
    fileName = ".editorconfig",
)
