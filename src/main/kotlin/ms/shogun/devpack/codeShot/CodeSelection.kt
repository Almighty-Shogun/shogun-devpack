package ms.shogun.devpack.codeShot

import com.intellij.util.DocumentUtil
import com.intellij.openapi.editor.ex.EditorEx

/**
 * Calculates selected editor text bounds for Code Shot rendering.
 *
 * @author Almighty-Shogun
 * @since 1.1.1
 */
object CodeSelection {
    /**
     * Finds the tight right edge of selected render text.
     *
     * @param editor Editor containing the selected render text.
     * @param startLine First logical line included in the screenshot.
     * @param endLine Exclusive logical line after the screenshot.
     *
     * @return Right edge in editor pixels.
     *
     * @author Almighty-Shogun
     * @since 1.1.1
     */
    fun textRightEdge(editor: EditorEx, startLine: Int, endLine: Int): Int {
        val document = editor.document
        val characters = document.charsSequence
        val fontMetrics = editor.contentComponent.getFontMetrics(editor.contentComponent.font)

        return (startLine until endLine).maxOf { line ->
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)

            if (lineStartOffset == lineEndOffset) {
                0
            } else {
                val lastCharacterOffset = lineEndOffset - 1
                val lineEndInlayWidth = editor.inlayModel
                    .getAfterLineEndElementsForLogicalLine(line)
                    .sumOf { inlay ->
                        inlay.widthInPixels
                    }

                editor.offsetToXY(lastCharacterOffset).x + fontMetrics.charWidth(characters[lastCharacterOffset]) + lineEndInlayWidth
            }
        }
    }

    /**
     * Calculates the common indentation across selected render text lines.
     *
     * @param editor Editor containing the selected render text.
     * @param startLine First logical line included in the screenshot.
     * @param endLine Exclusive logical line after the screenshot.
     *
     * @return Common indentation width in editor pixels.
     *
     * @author Almighty-Shogun
     * @since 1.1.1
     */
    fun commonLineIndent(editor: EditorEx, startLine: Int, endLine: Int): Int {
        var indentStart = Int.MAX_VALUE
        val document = editor.document

        for (line in startLine until endLine) {
            if (DocumentUtil.isLineEmpty(document, line)) continue

            val offset = DocumentUtil.getFirstNonSpaceCharOffset(document, line)
            val indent = editor.offsetToXY(offset).x

            if (indent < indentStart) {
                indentStart = indent
            }
        }

        return indentStart.takeUnless { indent ->
            indent == Int.MAX_VALUE
        } ?: 0
    }

}
