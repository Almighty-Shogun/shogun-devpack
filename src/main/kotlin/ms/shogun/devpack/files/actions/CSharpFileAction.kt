package ms.shogun.devpack.files.actions

import java.util.Properties

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiDirectory
import com.intellij.openapi.project.Project
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.ide.fileTemplates.FileTemplateManager

import ms.shogun.devpack.PluginIcons
import ms.shogun.devpack.utils.toKebabCase
import ms.shogun.devpack.files.TemplateKind
import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.files.BaseTemplateFileAction
import ms.shogun.devpack.utils.CSharpNamespaceResolver

/**
 * Creates C# classes and related paired interface files from templates.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CSharpFileAction : BaseTemplateFileAction(
    actionName = message("file.action.csharp.name"),
    dialogTitle = message("file.action.csharp.dialog")
) {
    override val templateKinds = listOf(
        TemplateKind(message("template.kind.service"), PluginIcons.csharp, "Service"),
        TemplateKind(message("template.kind.json-model"), PluginIcons.csharp, "JSON Model"),
        TemplateKind(message("template.kind.remote-command"), PluginIcons.csharp, "Remote Command"),
        TemplateKind(message("template.kind.console-command"), PluginIcons.csharp, "Console Command"),
        TemplateKind(message("template.kind.dependency-injection"), PluginIcons.csharp, "Dependency Injection"),
    )

    override fun createFromTemplate(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        requestedName: String
    ): PsiFile? {
        val namespace = CSharpNamespaceResolver.resolve(directory, project)
        val templateName = template.logicalName()

        return when (templateName) {
            "Dependency Injection" -> createClassAndInterface(project, directory, template, requestedName, namespace)
            else -> {
                val typeName = classFileName(templateName, requestedName)

                createCSharpFile(
                    project = project,
                    directory = directory,
                    template = template,
                    requestedName = requestedName,
                    fileName = typeName,
                    isInterface = false,
                    namespace = namespace
                )
            }
        }
    }

    /**
     * Creates a concrete class and matching interface for templates that need both.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory where both files should be created.
     * @param template Selected C# file template.
     * @param requestedName Requested C# type name.
     * @param namespace Namespace applied to both generated files.
     *
     * @return Created interface PSI file.
     * @throws RuntimeException When IntelliJ template creation fails.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createClassAndInterface(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        requestedName: String,
        namespace: String
    ): PsiFile {
        createCSharpFile(
            project = project,
            directory = directory,
            template = template,
            requestedName = requestedName,
            fileName = requestedName,
            isInterface = false,
            namespace = namespace
        )

        return createCSharpFile(
            project = project,
            directory = directory,
            template = template,
            requestedName = requestedName,
            fileName = "I$requestedName",
            isInterface = true,
            namespace = namespace
        )
    }

    /**
     * Creates one C# file and opens it in the editor.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory where the file should be created.
     * @param template Selected C# file template.
     * @param requestedName Requested C# type name used for template variables.
     * @param fileName Fixed output file name.
     * @param isInterface Whether the generated type should be an interface.
     * @param namespace Namespace applied to the generated file.
     *
     * @return Created PSI file.
     * @throws RuntimeException When IntelliJ template creation fails.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createCSharpFile(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        requestedName: String,
        fileName: String,
        isInterface: Boolean,
        namespace: String
    ): PsiFile {
        val createdFile = FileTemplateUtil.createFromTemplate(
            template,
            fileName,
            createProperties(project, requestedName, fileName, isInterface, namespace),
            directory
        ).containingFile

        FileEditorManager.getInstance(project).openFile(createdFile.virtualFile, true)

        return createdFile
    }

    /**
     * Builds C# template properties including namespace and type kind.
     *
     * @param project Current IntelliJ project.
     * @param requestedName Requested C# type name.
     * @param fileName Generated C# file and type name.
     * @param isInterface Whether the generated type should be an interface.
     * @param namespace Namespace applied to the generated file.
     *
     * @return Template properties passed to IntelliJ's template engine.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createProperties(
        project: Project,
        requestedName: String,
        fileName: String,
        isInterface: Boolean,
        namespace: String
    ): Properties =
        FileTemplateManager.getInstance(project).defaultProperties.apply {
            setProperty("NAME", fileName)
            setProperty("NAME_NORMALIZED", toKebabCase(requestedName))
            setProperty("NAMESPACE", namespace)
            setProperty("TYPE", if (isInterface) "interface" else "class")
        }

    /**
     * Applies C# suffix conventions for generated class file names.
     *
     * @param templateName Selected C# template name.
     * @param requestedName Requested C# type name.
     *
     * @return File name after applying the template-specific suffix.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun classFileName(templateName: String, requestedName: String): String =
        when (templateName) {
            "Service" -> "${baseTypeName(templateName, requestedName)}Service"
            "Console Command" -> "${baseTypeName(templateName, requestedName)}ConsoleCommand"
            "Remote Command" -> "${baseTypeName(templateName, requestedName)}RemoteCommand"
            else -> requestedName
        }

    /**
     * Removes a template-owned suffix from the requested type name before the template applies it again.
     *
     * @param templateName Selected C# template name.
     * @param requestedName Requested C# type name.
     *
     * @return Base type name used by templates.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun baseTypeName(templateName: String, requestedName: String): String =
        when (templateName) {
            "Service" -> requestedName.removeSuffix("Service")
            "Console Command" -> requestedName.removeSuffix("ConsoleCommand")
            "Remote Command" -> requestedName.removeSuffix("RemoteCommand")
            else -> requestedName
        }

    private fun FileTemplate.logicalName(): String = name.substringBeforeLast('.')
}
