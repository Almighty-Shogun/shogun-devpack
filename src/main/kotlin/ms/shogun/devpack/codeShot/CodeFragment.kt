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
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.ex.util.EditorUIUtil
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsScheme

/**
 * Swing component that paints prepared editor text for screenshot rendering.
 *
 * @param editor Editor instance to paint.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeFragment private constructor(private val editor: EditorEx) : JPanel() {
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

        val originalColorScheme = this.editor.colorsScheme
        val originalCaretRowShown = settings.isCaretRowShown
        val originalHighlightSelectionOccurrences = settings.isHighlightSelectionOccurrences

        val newRendering = this.editor is EditorImpl
        val savedScrollOffset = if (newRendering) 0 else this.editor.scrollingModel.horizontalScrollOffset

        val foldingModel = this.editor.foldingModel
        val isFoldingEnabled = foldingModel.isFoldingEnabled

        this.editor.colorsScheme = cleanScreenshotColorScheme(originalColorScheme)

        settings.isCaretRowShown = false
        settings.isHighlightSelectionOccurrences = false

        foldingModel.isFoldingEnabled = false

        val textImageWidth: Int
        val textImageHeight: Int
        val textImage: BufferedImage
        val renderScale = CodeScreenshotRenderer.getRenderScale(this.editor.component)

        try {
            val startLine = 0

            val document = this.editor.document
            val endLine = document.lineCount

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

            this.editor.component.setSize(textImageWidth, textImageHeight)
            this.editor.contentComponent.setSize(textImageWidth, textImageHeight)

            this.editor.component.doLayout()

            if (savedScrollOffset > 0) {
                this.editor.scrollingModel.scrollHorizontally(0)
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

            this.editor.colorsScheme = originalColorScheme
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
            val selectedText = CodeSelection.text(sourceEditor, startOffset, endOffset)
            val detachedEditor = DetachedCodeEditor.create(sourceEditor, selectedText)
            val backgroundColor = CodeScreenshotRenderer.getBackgroundColor(sourceEditor)

            try {
                detachedEditor.backgroundColor = backgroundColor

                val fragment = CodeFragment(detachedEditor)

                fragment.background = backgroundColor

                return fragment
            } finally {
                DetachedCodeEditor.release(detachedEditor)
            }
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
