package ms.shogun.devpack.settings.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagLayout
import java.awt.RenderingHints
import java.awt.GridBagConstraints

import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JPanel

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.intellij.icons.AllIcons

/**
 * Rounded settings notice used to highlight important informational, warning, or error messages.
 *
 * @param text Notice text shown inside the panel.
 * @param style Visual style used for icon and colors.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class SettingsNotice private constructor(text: String, private val style: Style) : JPanel(GridBagLayout()) {
    init {
        isOpaque = false
        border = JBUI.Borders.empty(10, 12)

        add(JLabel(style.icon), iconConstraints())
        add(
            SettingsDescription(text).apply {
                foreground = style.textColor
            },
            textConstraints(),
        )
    }

    override fun paintComponent(graphics: Graphics) {
        val graphics2d = graphics.create() as Graphics2D

        try {
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            graphics2d.color = style.backgroundColor
            graphics2d.fillRoundRect(0, 0, width - 1, height - 1, ARC_SIZE, ARC_SIZE)
            graphics2d.color = style.borderColor
            graphics2d.drawRoundRect(0, 0, width - 1, height - 1, ARC_SIZE, ARC_SIZE)
        } finally {
            graphics2d.dispose()
        }

        super.paintComponent(graphics)
    }

    /**
     * Builds constraints for the notice icon.
     *
     * @return Grid bag constraints for the icon.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun iconConstraints(): GridBagConstraints =
        GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            anchor = GridBagConstraints.NORTHWEST
            insets = JBUI.insetsRight(10)
        }

    /**
     * Builds constraints for the notice text.
     *
     * @return Grid bag constraints for the text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun textConstraints(): GridBagConstraints =
        GridBagConstraints().apply {
            gridx = 1
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
        }

    /**
     * Visual style for a settings notice.
     *
     * @param icon Notice icon.
     * @param backgroundColor Notice background color.
     * @param borderColor Notice border color.
     * @param textColor Notice text color.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    enum class Style(
        val icon: Icon,
        val backgroundColor: Color,
        val borderColor: Color,
        val textColor: Color
    ) {
        INFO(
            icon = AllIcons.General.NotificationInfo,
            backgroundColor = JBColor(Color(0xEAF6FF), Color(0x102F44)),
            borderColor = JBColor(Color(0x5CAEE8), Color(0x2D7FAE)),
            textColor = JBColor(Color(0x174E72), Color(0xB9DFF7)),
        ),
        WARNING(
            icon = AllIcons.General.NotificationWarning,
            backgroundColor = JBColor(Color(0xFFF8E1), Color(0x3A2D0D)),
            borderColor = JBColor(Color(0xD8A400), Color(0xA97800)),
            textColor = JBColor(Color(0x684B00), Color(0xF1D27A)),
        ),
        ERROR(
            icon = AllIcons.General.NotificationError,
            backgroundColor = JBColor(Color(0xFFF0F1), Color(0x49131A)),
            borderColor = JBColor(Color(0xE04755), Color(0xD73745)),
            textColor = JBColor(Color(0x8A1F2D), Color(0xFFB6C0)),
        ),
    }

    companion object {
        private val ARC_SIZE: Int
            get() = JBUI.scale(8)

        /**
         * Creates an informational notice.
         *
         * @param text Notice text.
         *
         * @return Informational settings notice.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun info(text: String): SettingsNotice = SettingsNotice(text, Style.INFO)

        /**
         * Creates a warning notice.
         *
         * @param text Notice text.
         *
         * @return Warning settings notice.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun warning(text: String): SettingsNotice = SettingsNotice(text, Style.WARNING)

        /**
         * Creates an error notice.
         *
         * @param text Notice text.
         *
         * @return Error settings notice.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun error(text: String): SettingsNotice = SettingsNotice(text, Style.ERROR)
    }
}
