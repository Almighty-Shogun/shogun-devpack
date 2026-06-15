package ms.shogun.devpack.files.actions

import java.util.Properties

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager

import ms.shogun.devpack.PluginIcons
import ms.shogun.devpack.files.TemplateKind
import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.utils.toPascalCase
import ms.shogun.devpack.files.BaseTemplateFileAction

/**
 * Creates Vue components, composables, stores, and bundle exports.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class VueFileAction : BaseTemplateFileAction(
    actionName = message("file.action.vue.name"),
    dialogTitle = message("file.action.vue.dialog")
) {
    private val exportRegex = Regex("""export\s+\{\s*(.+?)\s*}\s+from\s+['"](.+?)['"]""")

    override val templateKinds = listOf(
        TemplateKind(message("template.kind.vue-component"), PluginIcons.vue, "Vue Component"),
        TemplateKind(message("template.kind.vue-component-bundle"), PluginIcons.vue, "Vue Component Bundle"),
        TemplateKind(message("template.kind.vue-store"), PluginIcons.typescript, "Vue Store"),
        TemplateKind(message("template.kind.vue-composable"), PluginIcons.typescript, "Vue Composable"),
    )

    override fun createFromTemplate(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        requestedName: String
    ): PsiFile? {
        val templateName = template.logicalName()

        return when (templateName) {
            "Vue Component Bundle" -> createComponentBundle(project, directory, template, requestedName)
            "Vue Component", "Vue Store", "Vue Composable" -> createSingleFile(project, directory, template, templateName, requestedName)
            else -> null
        }
    }

    /**
     * Creates a single Vue-related file and updates the parent index export when present.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory where the file should be created.
     * @param template Selected Vue-related file template.
     * @param templateName Logical Vue template name.
     * @param requestedName Requested component, store, or composable name.
     *
     * @return Created PSI file.
     * @throws RuntimeException When IntelliJ template creation fails.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createSingleFile(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        templateName: String,
        requestedName: String
    ): PsiFile {
        val fileName = vueFileName(templateName, requestedName)
        val createdFile = createPsiFile(project, directory, template, fileName, createVueProperties(project, requestedName, fileName))

        appendIndexExport(project, directory, templateName, requestedName, fileName)

        return createdFile
    }

    /**
     * Creates a component directory with a component file and bundle index export.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory where the component bundle directory should be created.
     * @param template Selected Vue component template.
     * @param componentName Requested component name.
     *
     * @return Created component PSI file.
     * @throws RuntimeException When directory or template creation fails.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createComponentBundle(
        project: Project,
        directory: PsiDirectory,
        template: FileTemplate,
        componentName: String
    ): PsiFile {
        val componentDirectoryName = componentName.lowercase()
        val componentDirectory = directory.createSubdirectory(componentDirectoryName)
        componentDirectory.createFile("index.ts")

        val componentFile = createPsiFile(project, componentDirectory, template, componentName, createVueProperties(project, componentName, componentName))

        upsertIndexExport(
            project = project,
            directory = componentDirectory,
            exportName = "default",
            importPath = "./$componentName.vue",
            exportStatement = "export { default } from './$componentName.vue'",
        )

        upsertIndexExport(
            project = project,
            directory = directory,
            exportName = componentName,
            importPath = "./$componentDirectoryName",
            exportStatement = "export { default as $componentName } from './$componentDirectoryName'",
        )

        FileEditorManager.getInstance(project).openFile(componentFile.virtualFile, true)

        return componentFile
    }

    /**
     * Adds or updates the relevant export line in an existing index file.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory that may contain `index.ts`.
     * @param templateName Logical Vue template name.
     * @param componentName Requested component, store, or composable name.
     * @param fileName Generated file name used by the export.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun appendIndexExport(
        project: Project,
        directory: PsiDirectory,
        templateName: String,
        componentName: String,
        fileName: String
    ) {
        when (templateName) {
            "Vue Store", "Vue Composable" -> upsertIndexExport(project, directory, fileName, "./$fileName", "export { $fileName } from './$fileName'")
            "Vue Component" -> upsertIndexExport(
                project = project,
                directory = directory,
                exportName = componentName,
                importPath = "./$componentName.vue",
                exportStatement = "export { default as $componentName } from './$componentName.vue'"
            )

            else -> return
        }
    }

    /**
     * Inserts a missing export or collapses duplicate exports to one canonical line.
     *
     * @param project Current IntelliJ project.
     * @param directory Directory that may contain `index.ts`.
     * @param exportName Symbol expected to be exported.
     * @param importPath Import path expected in the export line.
     * @param exportStatement Canonical export statement to keep or insert.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun upsertIndexExport(
        project: Project,
        directory: PsiDirectory,
        exportName: String,
        importPath: String,
        exportStatement: String
    ) {
        val indexFile = directory.findFile("index.ts") ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(indexFile) ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            val lines = document.text.lineSequence().toMutableList()
            val matchingIndexes = lines.withIndex()
                .filter { (_, line) -> line.isExportFor(exportName, importPath) }
                .map { it.index }

            when {
                matchingIndexes.isEmpty() -> {
                    val prefix = if (document.text.isBlank() || document.text.endsWith("\n")) "" else "\n"
                    document.insertString(document.textLength, "$prefix$exportStatement\n")
                }

                else -> {
                    lines[matchingIndexes.first()] = exportStatement
                    matchingIndexes.drop(1).asReversed().forEach(lines::removeAt)
                    document.setText(lines.joinToString("\n").trimEnd() + "\n")
                }
            }

            PsiDocumentManager.getInstance(project).commitDocument(document)
        }
    }

    /**
     * Applies Vue naming conventions for generated file names.
     *
     * @param templateName Logical Vue template name.
     * @param requestedName Requested component, store, or composable name.
     *
     * @return File name after applying Vue naming conventions.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun vueFileName(templateName: String, requestedName: String): String =
        when (templateName) {
            "Vue Composable" -> "use${vueBaseName(requestedName)}"
            "Vue Store" -> "use${vueBaseName(requestedName)}Store"
            else -> requestedName
        }

    /**
     * Builds Vue template properties, including composable function and return type names.
     *
     * @param project Current IntelliJ project.
     * @param requestedName Requested component, store, or composable name.
     * @param generatedName Generated file and export name.
     *
     * @return Template properties passed to IntelliJ's template engine.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createVueProperties(project: Project, requestedName: String, generatedName: String): Properties =
        super.createProperties(project, requestedName).apply {
            val composableName = "use${vueBaseName(requestedName)}"

            setProperty("NAME", generatedName)
            setProperty("COMPOSABLE_NAME", composableName)
            setProperty("COMPOSABLE_RETURN_TYPE", "Use${vueBaseName(requestedName)}")
        }

    /**
     * Normalizes a Vue keyword before template-specific prefixes or suffixes are applied.
     *
     * @param requestedName Requested Vue keyword.
     *
     * @return PascalCase keyword without existing `use` or `Store` wrappers.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun vueBaseName(requestedName: String): String {
        val pascalName = toPascalCase(requestedName)
        val withoutUse = if (pascalName.length > 3 && pascalName.startsWith("Use") && pascalName[3].isUpperCase()) {
            pascalName.removePrefix("Use")
        } else {
            pascalName
        }

        return withoutUse.removeSuffix("Store")
    }

    /**
     * Checks if an export line already exposes the same symbol or path.
     *
     * @param exportName Symbol expected to be exported.
     * @param importPath Import path expected in the export line.
     *
     * @return `true` when the line exports the same symbol or import path.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun String.isExportFor(exportName: String, importPath: String): Boolean {
        val match = exportRegex.matchEntire(trim().removeSuffix(";")) ?: return false

        val exportedSymbols = match.groupValues[1]
        val exportedPath = match.groupValues[2]

        return exportedPath == importPath || exportedSymbols.contains(Regex("""(^|[,\s])$exportName([,\s]|$)"""))
    }

    private fun FileTemplate.logicalName(): String = name.substringBeforeLast('.')
}
