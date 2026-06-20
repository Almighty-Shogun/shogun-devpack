package ms.shogun.devpack.codeShot

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.fileEditor.FileDocumentManager

/**
 * Creates temporary editors for Code Shot rendering.
 *
 * @author Almighty-Shogun
 * @since 1.1.1
 */
object DetachedCodeEditor {
    /**
     * Creates a detached read-only editor using the source editor's project, file type, and colors.
     *
     * @param sourceEditor Editor whose rendering context should be reused.
     * @param text Text to render.
     *
     * @return Temporary editor used only during screenshot rendering.
     *
     * @author Almighty-Shogun
     * @since 1.1.1
     */
    fun create(sourceEditor: EditorEx, text: String): EditorEx {
        val editorFactory = EditorFactory.getInstance()
        val fileType = FileDocumentManager.getInstance()
            .getFile(sourceEditor.document)
            ?.fileType
            ?: PlainTextFileType.INSTANCE

        val document = editorFactory.createDocument(text)
        val editor = sourceEditor.project
            ?.let { project ->
                editorFactory.createEditor(document, project, fileType, true)
            }
            ?: editorFactory.createViewer(document)

        return (editor as EditorEx).apply {
            colorsScheme = sourceEditor.colorsScheme
        }
    }

    /**
     * Releases a temporary Code Shot editor.
     *
     * @param editor Temporary editor to release.
     *
     * @author Almighty-Shogun
     * @since 1.1.1
     */
    fun release(editor: EditorEx) {
        EditorFactory.getInstance().releaseEditor(editor)
    }
}
