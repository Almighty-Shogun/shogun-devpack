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
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.ex.util.EditorUIUtil
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.editor.colors.EditorColorsScheme

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
class CodeFragment(private val editor: EditorEx, private val startOffset: Int, private val endOffset: Int) : JPanel() {
    init {
        this.editor.isPurePaintingMode = true

        try {
            this.withScreenshotRenderingState {
                this.doInitialize()
            }
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
            val document = this.editor.document
            val selectionEndOffset = endOffset.coerceAtLeast(startOffset)
            val lastSelectedOffset = (selectionEndOffset - 1).coerceAtLeast(startOffset)
            val startLine = document.getLineNumber(startOffset)
            val endLine = document.getLineNumber(lastSelectedOffset) + 1
            val widthAdjustment = if (newRendering) EditorUtil.getSpaceWidth(Font.PLAIN, editor) else 0
            val endRangeOffset = if (endLine < document.lineCount) document.getLineEndOffset(max(0, endLine - 1)) else document.textLength
            val indentStart = commonSelectedLineIndent(startLine, endLine)
            val cropStart = (indentStart - HORIZONTAL_RENDER_BLEED).coerceAtLeast(0)
            val cropBleed = indentStart - cropStart

            textImageWidth = min(
                this.editor.getMaxWidthInRange(document.getLineStartOffset(startLine), endRangeOffset) + widthAdjustment - cropStart + HORIZONTAL_RENDER_BLEED,
                CodeScreenshotRenderer.getWidthLimit(editor),
            )

            val p1 = this.editor.logicalPositionToXY(LogicalPosition(startLine, 0))
            val p2 = this.editor.logicalPositionToXY(LogicalPosition(max(endLine, startLine - 1), 0))

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

            textGraphics.translate(-cropStart, -p1.y)
            textGraphics.setClip(cropStart, p1.y, textImageWidth + cropBleed, textImageHeight)

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

    /**
     * Calculates the common indentation across selected lines.
     *
     * @param startLine First logical line included in the screenshot.
     * @param endLine Exclusive logical line after the screenshot.
     *
     * @return Common indentation width in editor pixels.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun commonSelectedLineIndent(startLine: Int, endLine: Int): Int {
        var indentStart = Int.MAX_VALUE
        val document = this.editor.document

        for (line in startLine until endLine) {
            if (DocumentUtil.isLineEmpty(document, line)) continue

            val offset = DocumentUtil.getFirstNonSpaceCharOffset(document, line)
            val indent = this.editor.offsetToXY(offset).x

            if (indent < indentStart) {
                indentStart = indent
            }
        }

        return indentStart.takeUnless { indent ->
            indent == Int.MAX_VALUE
        } ?: 0
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
            val editorEx = editor as EditorEx
            val old = editorEx.backgroundColor
            val backgroundColor = CodeScreenshotRenderer.getBackgroundColor(editor)

            editorEx.backgroundColor = backgroundColor

            try {
                val fragment = CodeFragment(editor, startOffset, endOffset)

                fragment.background = backgroundColor

                return fragment
            } finally {
                editorEx.backgroundColor = old
            }
        }
    }

    /**
     * Applies temporary editor state that keeps screenshots free of transient IDE highlights.
     *
     * @param action Rendering action to run while temporary state is active.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun withScreenshotRenderingState(action: () -> Unit) {
        val settings = this.editor.settings
        val originalColorScheme = this.editor.colorsScheme
        val originalCaretRowShown = settings.isCaretRowShown
        val originalHighlightSelectionOccurrences = settings.isHighlightSelectionOccurrences

        this.editor.colorsScheme = cleanScreenshotColorScheme(originalColorScheme)

        settings.isCaretRowShown = false
        settings.isHighlightSelectionOccurrences = false

        try {
            action()
        } finally {
            settings.isHighlightSelectionOccurrences = originalHighlightSelectionOccurrences
            settings.isCaretRowShown = originalCaretRowShown

            this.editor.colorsScheme = originalColorScheme
        }
    }

    /**
     * Creates a color scheme clone without editor-state highlights that should not appear in screenshots.
     *
     * @param colorScheme Active editor color scheme.
     *
     * @return Screenshot color scheme.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun cleanScreenshotColorScheme(colorScheme: EditorColorsScheme): EditorColorsScheme =
        this.editor.createBoundColorSchemeDelegate(colorScheme).apply {
            setAttributes(EditorColors.IDENTIFIER_UNDER_CARET_ATTRIBUTES, TextAttributes())
            setAttributes(EditorColors.WRITE_IDENTIFIER_UNDER_CARET_ATTRIBUTES, TextAttributes())
            setAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES, TextAttributes())
            setAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES, TextAttributes())
            setAttributes(EditorColors.TEXT_SEARCH_RESULT_ATTRIBUTES, TextAttributes())
            setAttributes(EditorColors.LIVE_TEMPLATE_ATTRIBUTES, TextAttributes())
            setAttributes(EditorColors.LIVE_TEMPLATE_INACTIVE_SEGMENT, TextAttributes())
            setAttributes(CodeInsightColors.MATCHED_BRACE_ATTRIBUTES, TextAttributes())
            setAttributes(CodeInsightColors.UNMATCHED_BRACE_ATTRIBUTES, TextAttributes())
            setAttributes(CodeInsightColors.BLINKING_HIGHLIGHTS_ATTRIBUTES, TextAttributes())
            setColor(EditorColors.CARET_ROW_COLOR, defaultBackground)
        }

}
