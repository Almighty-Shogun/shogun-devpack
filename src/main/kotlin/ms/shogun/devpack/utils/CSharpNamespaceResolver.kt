package ms.shogun.devpack.utils

import java.io.ByteArrayInputStream

import javax.xml.parsers.DocumentBuilderFactory

import com.intellij.psi.PsiDirectory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.guessProjectDir

import org.w3c.dom.Element

/**
 * Resolves a C# namespace from the nearest project file and selected directory.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CSharpNamespaceResolver {
    /**
     * Builds the namespace for a newly generated C# file.
     *
     * @param directory Directory where the C# file will be created.
     * @param project Current IntelliJ project.
     *
     * @return Resolved namespace for the new file.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun resolve(directory: PsiDirectory, project: Project): String {
        val selectedDirectory = directory.virtualFile
        val projectFile = findNearestProjectFile(selectedDirectory)

        val namespaceRoot = projectFile?.let(::readRootNamespace) ?: projectFile?.nameWithoutExtension ?: project.name
        val namespaceRootDirectory = projectFile?.parent ?: project.guessProjectDir()

        val relativeSegments = namespaceRootDirectory
            ?.let { selectedDirectory.relativePathFrom(it) }
            .orEmpty()
            .split('/', '\\')
            .filter { it.isNotBlank() }

        return (namespaceRoot.split('.') + relativeSegments)
            .mapNotNull(::toCSharpIdentifier)
            .joinToString(".")
            .ifBlank { toCSharpIdentifier(directory.name) ?: "Generated" }
    }

    /**
     * Walks upward from a directory until a .csproj file is found.
     *
     * @param directory Directory where the search starts.
     *
     * @return Nearest `.csproj` file, or `null` when none exists above the directory.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun findNearestProjectFile(directory: VirtualFile): VirtualFile? {
        var current: VirtualFile? = directory

        while (current != null) {
            current.children
                .firstOrNull { !it.isDirectory && it.extension.equals("csproj", ignoreCase = true) }
                ?.let { return it }

            current = current.parent
        }

        return null
    }

    /**
     * Reads the RootNamespace property from a project file without resolving external XML entities.
     *
     * @param projectFile C# project file to inspect.
     *
     * @return Root namespace from the project file, or `null` when it is absent or unreadable.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun readRootNamespace(projectFile: VirtualFile): String? =
        runCatching {
            val documentBuilder = DocumentBuilderFactory.newInstance().apply {
                isNamespaceAware = false
                setFeature("https://apache.org/xml/features/disallow-doctype-decl", true)
                setFeature("https://xml.org/sax/features/external-general-entities", false)
                setFeature("https://xml.org/sax/features/external-parameter-entities", false)
            }.newDocumentBuilder()

            val document = ByteArrayInputStream(projectFile.contentsToByteArray()).use(documentBuilder::parse)
            val nodes = document.getElementsByTagName("RootNamespace")

            (0 until nodes.length)
                .asSequence()
                .mapNotNull { nodes.item(it) as? Element }
                .map { it.textContent.trim() }
                .firstOrNull { it.isNotBlank() }
        }.getOrNull()

    /**
     * Returns this file path relative to a known parent directory.
     *
     * @param parent Parent directory used as the relative path root.
     *
     * @return Relative path from `parent`, or `null` when this file is not under `parent`.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun VirtualFile.relativePathFrom(parent: VirtualFile): String? {
        val parentPath = parent.path.trimEnd('/', '\\')
        val childPath = path

        return childPath
            .takeIf { it == parentPath || it.startsWith("$parentPath/") || it.startsWith("$parentPath\\") }
            ?.removePrefix(parentPath)
            ?.trimStart('/', '\\')
    }

    /**
     * Converts arbitrary path or project-name text into a legal C# namespace segment.
     *
     * @param value Text to normalize into a C# identifier.
     *
     * @return Legal C# namespace segment, or `null` when no valid characters remain.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun toCSharpIdentifier(value: String): String? {
        val identifier = value
            .trim()
            .replace(Regex("[^A-Za-z0-9_]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')

        if (identifier.isBlank()) return null

        return if (identifier.first().isDigit()) "_$identifier" else identifier
    }
}
