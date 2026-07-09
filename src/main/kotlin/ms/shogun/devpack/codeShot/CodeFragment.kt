package ms.shogun.devpack.codeShot

import java.awt.Graphics
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.BorderLayout
import java.awt.image.BufferedImage

import javax.swing.JPanel
import javax.swing.JComponent

import kotlin.math.max

import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.StartupUiUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ex.util.EditorUIUtil

/**
 * Swing component that paints a selected editor range for screenshot rendering.
 *
 * @param editor Editor instance to paint.
 * @param startOffset First selected document offset.
 * @param endOffset Exclusive selected document offset.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeFragment private constructor(
    private val editor: EditorEx,
    private val startOffset: Int,
    private val endOffset: Int,
) : JPanel() {
    init {
        this.editor.isPurePaintingMode = true

        try {
            this.doInitialize()
        } finally {
            this.editor.isPurePaintingMode = false
        }
    }

    /**
     * Captures editor, gutter, and line-range painting into reusable image components.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun doInitialize() {
        val settings = this.editor.settings

        val originalCaretRowShown = settings.isCaretRowShown
        val originalHighlightSelectionOccurrences = settings.isHighlightSelectionOccurrences

        val selectionModel = this.editor.selectionModel

        val originalSelectionStart = selectionModel.selectionStart
        val originalSelectionEnd = selectionModel.selectionEnd

        val hadSelection = selectionModel.hasSelection()

        val foldingModel = this.editor.foldingModel
        val isFoldingEnabled = foldingModel.isFoldingEnabled

        settings.isCaretRowShown = false
        settings.isHighlightSelectionOccurrences = false

        selectionModel.removeSelection()

        foldingModel.isFoldingEnabled = false

        val textImageWidth: Int
        val textImageHeight: Int
        val textImage: BufferedImage
        val renderScale = CodeScreenshotRenderer.getRenderScale(this.editor.component)

        try {
            val document = this.editor.document

            val selectionEndOffset = endOffset.coerceAtLeast(startOffset)
            val lastSelectedOffset = (selectionEndOffset - 1).coerceAtLeast(startOffset)

            val startLine = document.getLineNumber(startOffset)
            val endLine = document.getLineNumber(lastSelectedOffset) + 1

            val indentStart = CodeSelection.commonLineIndent(this.editor, startLine, endLine)

            val cropStart = (indentStart - HORIZONTAL_RENDER_BLEED).coerceAtLeast(0)
            val cropBleed = indentStart - cropStart

            val textRightEdge = CodeSelection.textRightEdge(this.editor, startLine, endLine)

            textImageWidth = (textRightEdge - cropStart + HORIZONTAL_RENDER_BLEED).coerceAtLeast(1)

            val topPoint = this.editor.logicalPositionToXY(LogicalPosition(startLine, 0))
            val bottomPoint = this.editor.logicalPositionToXY(LogicalPosition(max(endLine, startLine - 1), 0))

            textImageHeight = if (bottomPoint.y - topPoint.y == 0) {
                this.editor.lineHeight
            } else {
                bottomPoint.y - topPoint.y
            }

            textImage = UIUtil.createImage(
                this.editor.contentComponent,
                scaledSize(textImageWidth, renderScale),
                scaledSize(textImageHeight, renderScale),
                BufferedImage.TYPE_INT_RGB
            )

            val textGraphics = textImage.graphics as Graphics2D

            EditorUIUtil.setupAntialiasing(textGraphics)

            textGraphics.scale(renderScale.toDouble(), renderScale.toDouble())

            textGraphics.translate(-cropStart, -topPoint.y)
            textGraphics.setClip(cropStart, topPoint.y, textImageWidth + cropBleed, textImageHeight)

            val wasVisible = this.editor.setCaretVisible(false)

            try {
                this.editor.contentComponent.paint(textGraphics)
            } finally {
                if (wasVisible) {
                    this.editor.setCaretVisible(true)
                }
            }
        } finally {
            foldingModel.isFoldingEnabled = isFoldingEnabled

            settings.isCaretRowShown = originalCaretRowShown
            settings.isHighlightSelectionOccurrences = originalHighlightSelectionOccurrences

            if (hadSelection) {
                selectionModel.setSelection(originalSelectionStart, originalSelectionEnd)
            }
        }

        val component = object : JComponent() {
            override fun getPreferredSize() = Dimension(textImage.width, textImage.height)

            override fun paintComponent(graphics: Graphics) {
                StartupUiUtil.drawImage(graphics, textImage, 0, 0, null)
            }
        }

        this.layout = BorderLayout()

        this.add(component)

        this.border = CodeScreenshotRenderer.createCodeFragmentBorder(this.editor)
    }

    companion object {
        private const val HORIZONTAL_RENDER_BLEED = 4

        /**
         * Scales a logical editor size to screenshot pixels.
         *
         * @param size Logical size.
         * @param scale Screenshot render scale.
         *
         * @return Pixel size.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        private fun scaledSize(size: Int, scale: Float): Int =
            (size * scale).toInt().coerceAtLeast(1)

        /**
         * Creates a screenshot-ready fragment from a selected editor range.
         *
         * @param editor Editor containing the selected range to capture.
         * @param startOffset First selected document offset.
         * @param endOffset Exclusive selected document offset.
         * @return Prepared code fragment component.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun createCodeFragmentComponent(editor: Editor, startOffset: Int, endOffset: Int): CodeFragment {
            val sourceEditor = editor as EditorEx
            val originalBackgroundColor = sourceEditor.backgroundColor
            val backgroundColor = CodeScreenshotRenderer.getBackgroundColor(sourceEditor)

            try {
                sourceEditor.backgroundColor = backgroundColor

                val fragment = CodeFragment(sourceEditor, startOffset, endOffset)

                fragment.background = backgroundColor

                return fragment
            } finally {
                sourceEditor.backgroundColor = originalBackgroundColor
            }
        }

    }

}
