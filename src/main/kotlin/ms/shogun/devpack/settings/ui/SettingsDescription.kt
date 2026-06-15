package ms.shogun.devpack.settings.ui

import java.awt.Dimension

import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.ui.components.JBTextArea

/**
 * Wrapping description text for settings pages that can shrink after the settings dialog is resized.
 *
 * @param text Description text to show.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class SettingsDescription(text: String) : JBTextArea(text.replace("<br>", "\n")) {
    init {
        foreground = UIUtil.getContextHelpForeground()
        font = UIUtil.getLabelFont()
        isEditable = false
        isFocusable = false
        isOpaque = false
        lineWrap = true
        wrapStyleWord = true
        border = JBUI.Borders.empty()
    }

    override fun getScrollableTracksViewportWidth(): Boolean = true

    override fun getMinimumSize(): Dimension = Dimension(0, super.getMinimumSize().height)

    override fun getPreferredSize(): Dimension {
        parent?.width?.takeIf { width ->
            width > 0
        }?.let { width ->
            setSize(width, Short.MAX_VALUE.toInt())
        }

        return Dimension(0, super.getPreferredSize().height)
    }
}
