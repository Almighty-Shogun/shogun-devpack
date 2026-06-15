package ms.shogun.devpack.files

import java.util.Properties

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiDirectory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.ide.fileTemplates.actions.AttributesDefaults
import com.intellij.ide.fileTemplates.ui.CreateFromTemplateDialog

import ms.shogun.devpack.utils.toKebabCase
import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.utils.PluginNotifications

/**
 * Base action for fixed-name files created from internal IntelliJ file templates.
 *
 * @param actionTextKey Resource bundle key for the visible action text.
 * @param templateName Internal file template name.
 * @param fileName Fixed file name to create.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseFixedTemplateFileAction(
    private val actionTextKey: String,
    private val templateName: String,
    private val fileName: String
) : AnAction(), DumbAware {

    private val notifications by lazy {
        PluginNotifications(message(actionTextKey))
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val directory = event.getData(LangDataKeys.IDE_VIEW)?.orChooseDirectory ?: return

        runCatching {
            val fileTemplate = FileTemplateManager.getInstance(project).getInternalTemplate(templateName)
            val properties = customizeProperties(project, createProperties(project))
                ?: return@runCatching null

            createPsiFile(project, directory, fileTemplate, properties)
        }.onSuccess { file ->
            file?.let {
                FileEditorManager.getInstance(project).openFile(it.virtualFile, true)
            }
        }.onFailure { error ->
            notifications.error(project, message("file.create.error", error.message.orEmpty()))
        }
    }

    override fun update(event: AnActionEvent) {
        val hasDirectory = event.getData(LangDataKeys.IDE_VIEW)?.directories?.isNotEmpty() == true

        event.presentation.text = message(actionTextKey)
        event.presentation.isEnabled = event.project != null && hasDirectory
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    /**
     * Allows subclasses to add template properties or cancel file creation.
     *
     * @param project Current IntelliJ project.
     * @param properties Template properties.
     *
     * @return Updated properties, or `null` when creation should be canceled.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    protected open fun customizeProperties(project: Project, properties: Properties): Properties? = properties

    /**
     * Builds template properties for the fixed output file.
     *
     * @param project Current IntelliJ project.
     *
     * @return Template properties passed to IntelliJ's template engine.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createProperties(project: Project): Properties {
        val name = fileName.removePrefix(".").substringBeforeLast('.')

        return FileTemplateManager.getInstance(project).defaultProperties.apply {
            setProperty("NAME", name)
            setProperty("NAME_NORMALIZED", toKebabCase(name))
        }
    }

    /**
     * Creates a PSI file from the internal IntelliJ file template.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory where the file should be created.
     * @param template File template to apply.
     * @param properties Template properties passed to IntelliJ's template engine.
     *
     * @return Created PSI file.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createPsiFile(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        properties: Properties
    ): PsiFile =
        CreateFromTemplateDialog(project, directory, template, AttributesDefaults(fileName).withFixedName(true), properties)
            .create()
            .containingFile
}
