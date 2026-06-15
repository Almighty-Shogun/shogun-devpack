package ms.shogun.devpack.codeShot

import java.awt.Font
import java.awt.Graphics
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.BorderLayout
import java.awt.image.BufferedImage

import javax.swing.JPanel
import javax.swing.JComponent

import kotlin.math.max
import kotlin.math.min

import com.intellij.util.ui.UIUtil
import com.intellij.util.DocumentUtil
import com.intellij.openapi.editor.Editor
import com.intellij.util.ui.StartupUiUtil
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.ex.util.EditorUIUtil

/**
 * Swing component that paints a fixed editor line range for screenshot rendering.
 *
 * @param editor Editor instance to paint.
 * @param startLine First logical line included in the fragment.
 * @param endLine Exclusive logical line after the fragment.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeFragment(private val editor: EditorEx, private val startLine: Int, private val endLine: Int) : JPanel() {
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
        val newRendering = this.editor is EditorImpl
        val savedScrollOffset = if (newRendering) 0 else this.editor.scrollingModel.horizontalScrollOffset

        val foldingModel = this.editor.foldingModel
        val isFoldingEnabled = foldingModel.isFoldingEnabled

        foldingModel.isFoldingEnabled = false

        val textImageWidth: Int
        val textImageHeight: Int
        val textImage: BufferedImage
        val renderScale = CodeScreenshotRenderer.getRenderScale(this.editor.component)

        try {
            var offsetColumn = Int.MAX_VALUE
            val document = this.editor.document
            val widthAdjustment = if (newRendering) EditorUtil.getSpaceWidth(Font.PLAIN, editor) else 0
            val endOffset = if (endLine < document.lineCount) document.getLineEndOffset(max(0, endLine - 1)) else document.textLength

            for (line in startLine until endLine) {
                if (DocumentUtil.isLineEmpty(document, line)) continue

                val offset = DocumentUtil.getFirstNonSpaceCharOffset(document, line) - document.getLineStartOffset(line)

                if (offset < offsetColumn) {
                    offsetColumn = offset
                }
            }

            val offsetStart = offsetColumn * EditorUtil.getSpaceWidth(Font.PLAIN, editor)

            textImageWidth = min(
                this.editor.getMaxWidthInRange(document.getLineStartOffset(startLine), endOffset) + widthAdjustment - offsetStart,
                CodeScreenshotRenderer.getWidthLimit(editor),
            )

            val p1 = this.editor.logicalPositionToXY(LogicalPosition(startLine, offsetColumn))
            val p2 = this.editor.logicalPositionToXY(LogicalPosition(max(endLine, startLine - 1), offsetColumn))

            textImageHeight = if (p2.y - p1.y == 0) this.editor.lineHeight else p2.y - p1.y

            if (savedScrollOffset > 0) {
                this.editor.scrollingModel.scrollHorizontally(0)
            }

            textImage = UIUtil.createImage(
                this.editor.contentComponent,
                scaledSize(textImageWidth, renderScale),
                scaledSize(textImageHeight, renderScale),
                BufferedImage.TYPE_INT_RGB,
            )

            val textGraphics = textImage.graphics as Graphics2D

            EditorUIUtil.setupAntialiasing(textGraphics)

            textGraphics.scale(renderScale.toDouble(), renderScale.toDouble())

            textGraphics.translate(-offsetStart, -p1.y)
            textGraphics.setClip(0, p1.y, textImageWidth + offsetStart, textImageHeight)

            val wasVisible = this.editor.setCaretVisible(false)

            this.editor.contentComponent.paint(textGraphics)

            if (wasVisible) {
                this.editor.setCaretVisible(true)
            }
        } finally {
            foldingModel.isFoldingEnabled = isFoldingEnabled
        }

        if (savedScrollOffset > 0) {
            this.editor.scrollingModel.scrollHorizontally(savedScrollOffset)
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
         * Creates a screenshot-ready fragment from a visible editor line range.
         *
         * @param editor Editor containing the line range to capture.
         * @param startLine First logical line included in the fragment.
         * @param endLine Exclusive logical line after the fragment.
         * @return Prepared code fragment component.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun createCodeFragmentComponent(editor: Editor, startLine: Int, endLine: Int): CodeFragment {
            val editorEx = editor as EditorEx
            val old = editorEx.backgroundColor
            val backgroundColor = CodeScreenshotRenderer.getBackgroundColor(editor)

            editorEx.backgroundColor = backgroundColor

            val fragment = CodeFragment(editor, startLine, endLine)

            fragment.background = backgroundColor

            editorEx.backgroundColor = old

            return fragment
        }
    }
}
