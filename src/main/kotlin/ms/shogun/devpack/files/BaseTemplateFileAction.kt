package ms.shogun.devpack.files

import java.util.Properties

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiDirectory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog

import ms.shogun.devpack.utils.toKebabCase
import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.utils.PluginNotifications
import ms.shogun.devpack.files.validators.FileNameValidator

/**
 * Base action for creating files from internal IntelliJ file templates.
 *
 * @param actionName Visible action name returned after template creation.
 * @param dialogTitle Title shown in the new-file dialog.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseTemplateFileAction(
    private val actionName: String,
    private val dialogTitle: String
) : CreateFileFromTemplateAction() {

    private val notifications = PluginNotifications(actionName)

    protected abstract val templateKinds: List<TemplateKind>

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String = actionName

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(dialogTitle)
            .setValidator(FileNameValidator)

        templateKinds.forEach { kind ->
            builder.addKind(kind.label, kind.icon, kind.templateName)
        }
    }

    override fun createFileFromTemplate(name: String, template: FileTemplate, directory: PsiDirectory): PsiFile? =
        runCatching {
            val target = resolveTargetDirectory(directory, FileUtilRt.getNameWithoutExtension(name))

            createFromTemplate(
                project = directory.project,
                directory = target.directory,
                template = template,
                requestedName = target.fileName,
            )
        }.getOrElse { error ->
            notifications.error(directory.project, message("file.create.error", error.message.orEmpty()))
            null
        }

    /**
     * Creates a file for the selected template kind.
     *
     * @param project Current IntelliJ project.
     * @param directory Resolved directory where the file should be created.
     * @param template Selected file template.
     * @param requestedName Final requested file name without nested path segments.
     *
     * @return Created PSI file, or `null` when this template should not create a file.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    protected open fun createFromTemplate(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        requestedName: String
    ): PsiFile? = createPsiFile(project, directory, template, requestedName, createProperties(project, requestedName))

    /**
     * Builds template properties shared by all template-based file actions.
     *
     * @param project Current IntelliJ project.
     * @param requestedName Final requested file name without nested path segments.
     *
     * @return Template properties passed to IntelliJ's template engine.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    protected open fun createProperties(project: Project, requestedName: String): Properties =
        FileTemplateManager.getInstance(project).defaultProperties.apply {
            setProperty("NAME", requestedName)
            setProperty("NAME_NORMALIZED", toKebabCase(requestedName))
        }

    /**
     * Creates a file from the selected template with a fixed output file name.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory where the file should be created.
     * @param template File template to apply.
     * @param fileName Fixed output file name.
     * @param properties Template properties passed to IntelliJ's template engine.
     *
     * @return Created PSI file.
     * @throws RuntimeException When IntelliJ template creation fails.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    protected fun createPsiFile(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        fileName: String,
        properties: Properties
    ): PsiFile =
        FileTemplateUtil.createFromTemplate(template, fileName, properties, directory)
            .containingFile

    /**
     * Splits names like `messages/IMessage` into target directories and final file name.
     *
     * @param baseDirectory Directory selected before nested path resolution.
     * @param requestedName Requested file name or nested path.
     *
     * @return Directory and file name that should be used for template creation.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun resolveTargetDirectory(baseDirectory: PsiDirectory, requestedName: String): TemplateFileTarget {
        val pathSegments = requestedName.split('/').filter { it.isNotBlank() }
        val fileName = pathSegments.lastOrNull() ?: requestedName

        val directory = pathSegments.dropLast(1).fold(baseDirectory) { currentDirectory, segment ->
            currentDirectory.findSubdirectory(segment) ?: currentDirectory.createSubdirectory(segment)
        }

        return TemplateFileTarget(directory, fileName)
    }

    private data class TemplateFileTarget(
        val directory: PsiDirectory,
        val fileName: String
    )
}
