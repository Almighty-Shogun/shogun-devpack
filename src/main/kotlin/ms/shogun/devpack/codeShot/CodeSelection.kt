package ms.shogun.devpack.codeShot

import com.intellij.util.DocumentUtil
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.editor.ex.EditorEx

/**
 * Extracts selected editor text in the shape Code Shot should render.
 *
 * @author Almighty-Shogun
 * @since 1.1.1
 */
object CodeSelection {
    /**
     * Extracts selected lines and removes the shared indentation from the selected block.
     *
     * @param editor Source editor.
     * @param startOffset First selected document offset.
     * @param endOffset Exclusive selected document offset.
     *
     * @return Text rendered inside the detached screenshot editor.
     *
     * @author Almighty-Shogun
     * @since 1.1.1
     */
    fun text(editor: EditorEx, startOffset: Int, endOffset: Int): String {
        val document = editor.document

        val selectionEndOffset = endOffset.coerceAtLeast(startOffset)
        val lastSelectedOffset = (selectionEndOffset - 1).coerceAtLeast(startOffset)

        val startLine = document.getLineNumber(startOffset)
        val endLine = document.getLineNumber(lastSelectedOffset) + 1

        val lines = (startLine until endLine).map { line ->
            document.getText(TextRange(document.getLineStartOffset(line), document.getLineEndOffset(line)))
        }

        return lines.removeCommonIndent().joinToString("\n")
    }

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

                editor.offsetToXY(lastCharacterOffset).x + fontMetrics.charWidth(characters[lastCharacterOffset])
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

    /**
     * Removes shared leading whitespace from non-empty lines.
     *
     * @return Lines without source-context indentation.
     *
     * @author Almighty-Shogun
     * @since 1.1.1
     */
    private fun List<String>.removeCommonIndent(): List<String> {
        val indent = this
            .filter { line ->
                line.isNotBlank()
            }
            .map { line ->
                line.takeWhile { character ->
                    character == ' ' || character == '\t'
                }
            }
            .reduceOrNull { commonIndent, lineIndent ->
                commonIndent.commonPrefixWith(lineIndent)
            }
            .orEmpty()

        if (indent.isEmpty()) {
            return this
        }

        return map { line ->
            line.removePrefix(indent)
        }
    }
}
