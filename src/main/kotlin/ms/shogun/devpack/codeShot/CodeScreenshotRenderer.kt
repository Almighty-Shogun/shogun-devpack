package ms.shogun.devpack.codeShot

import java.awt.Color
import java.awt.Component
import java.awt.image.BufferedImage

import javax.swing.BorderFactory
import javax.swing.border.CompoundBorder

import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ImageUtil
import com.intellij.ide.ui.UISettings
import com.intellij.ui.scale.JBUIScale
import com.intellij.openapi.editor.Editor

/**
 * Renders prepared code fragments into clipboard-ready images.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodeScreenshotRenderer {
    /**
     * Paints a fragment into a buffered image.
     *
     * @param fragment Prepared code fragment component to render.
     *
     * @return Buffered image containing the rendered code fragment.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun render(fragment: CodeFragment): BufferedImage {
        val size = fragment.preferredSize

        fragment.size = size
        fragment.doLayout()

        val image = ImageUtil.createImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics

        UISettings.setupAntialiasing(graphics)
        fragment.printAll(graphics)

        graphics.dispose()

        return image
    }

    /**
     * Creates the screenshot padding and border using the editor background.
     *
     * @param editor Editor whose color scheme should drive the border color.
     *
     * @return Compound border used around rendered screenshots.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun createCodeFragmentBorder(editor: Editor): CompoundBorder {
        val color = getBackgroundColor(editor)

        val outsideBorder = JBUI.Borders.customLine(color, 6, 12, 6, 12)
        val insideBorder = JBUI.Borders.empty(1)

        return BorderFactory.createCompoundBorder(outsideBorder, insideBorder)
    }

    /**
     * Resolves the background color used for screenshot rendering.
     *
     * @param editor Editor whose color scheme should be inspected.
     * @return Color used as the screenshot background.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun getBackgroundColor(editor: Editor): Color = editor.colorsScheme.defaultBackground

    /**
     * Returns the pixel scale used when painting editor contents into a screenshot.
     *
     * @param component Component used to inspect the current system scale.
     *
     * @return Screenshot render scale.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun getRenderScale(component: Component): Float =
        JBUIScale.sysScale(component).coerceAtLeast(MINIMUM_RENDER_SCALE)

    private const val MINIMUM_RENDER_SCALE = 2.0f
}
