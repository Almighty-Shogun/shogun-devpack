package ms.shogun.devpack.settings.ui

import java.awt.GridBagConstraints

import javax.swing.JLabel
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants

import com.intellij.util.ui.JBUI
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBPasswordField

/**
 * Shared Swing helpers used by plugin settings pages.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object SettingsUi {
    /**
     * Creates a regular settings label.
     *
     * @param text Label text.
     *
     * @return Label component.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun label(text: String): JLabel = JLabel(text)

    /**
     * Creates subdued informational text that wraps to the available settings width.
     *
     * @param text Informational text.
     *
     * @return Text component.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun description(text: String): JBTextArea = SettingsDescription(text)

    /**
     * Creates a JetBrains-style titled separator for a settings section.
     *
     * @param text Section title.
     *
     * @return Titled separator component.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun sectionSeparator(text: String): TitledSeparator = TitledSeparator(text)

    /**
     * Converts blank password input into a nullable secret value.
     *
     * @param passwordField Password field containing a secret value.
     *
     * @return Trimmed secret, or `null` when blank.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun normalizedPassword(passwordField: JBPasswordField?): String? =
        passwordField
            ?.password
            ?.concatToString()
            ?.trim()
            ?.let { value ->
                value.ifEmpty {
                    null
                }
            }

    /**
     * Converts blank text field input into a nullable setting value.
     *
     * @param textField Text field containing a setting value.
     *
     * @return Trimmed text, or `null` when blank.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun normalizedText(textField: JBTextField?): String? =
        textField
            ?.text
            ?.trim()
            ?.ifEmpty {
                null
            }

    /**
     * Converts blank text area input into a nullable setting value.
     *
     * @param textArea Text area containing a setting value.
     *
     * @return Trimmed text, or `null` when blank.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun normalizedText(textArea: JBTextArea?): String? =
        textArea
            ?.text
            ?.trim()
            ?.ifEmpty {
                null
            }

    /**
     * Reads a text field with fallback for blank input.
     *
     * @param textField Text field containing a setting value.
     * @param fallback Fallback value.
     *
     * @return Trimmed text, or fallback when blank.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun nonBlankText(textField: JBTextField?, fallback: String): String =
        textField
            ?.text
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            } ?: fallback

    /**
     * Creates a wrapping text area for multiline settings values.
     *
     * @param rows Visible row count.
     *
     * @return Wrapping settings text area.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun wrappingTextArea(rows: Int): JBTextArea =
        object : JBTextArea() {
            override fun getScrollableTracksViewportWidth(): Boolean = true
        }.apply {
            this.rows = rows
            lineWrap = true
            wrapStyleWord = true
        }

    /**
     * Wraps a text area in a scroll pane that prevents horizontal scrolling.
     *
     * @param textArea Text area to wrap.
     *
     * @return Scroll pane for multiline settings values.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun verticalScrollPane(textArea: JBTextArea): JComponent =
        JBScrollPane(textArea).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        }

    /**
     * Builds constraints for full-width rows.
     *
     * @param row Grid row.
     * @param topInset Top inset for the row.
     * @param leftInset Left inset for the row.
     * @param bottomInset Bottom inset for the row.
     * @param gridWidth Number of columns occupied by the row.
     *
     * @return Grid bag constraints for full-width components.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun fullWidthConstraints(
        row: Int,
        topInset: Int = 0,
        leftInset: Int = 0,
        bottomInset: Int = 0,
        gridWidth: Int = 1
    ): GridBagConstraints =
        GridBagConstraints().apply {
            gridx = 0
            gridy = row
            gridwidth = gridWidth
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.WEST
            insets = JBUI.insets(topInset, leftInset, bottomInset, 0)
        }

    /**
     * Builds constraints for a grid cell in a multi-column settings row.
     *
     * @param row Grid row.
     * @param column Grid column.
     * @param weight Horizontal weight.
     * @param topInset Top inset for the cell.
     * @param leftInset Left inset for the cell.
     * @param bottomInset Bottom inset for the cell.
     * @param rightInset Right inset for the cell.
     * @param fill Fill behavior.
     *
     * @return Grid bag constraints for a settings grid cell.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun cellConstraints(
        row: Int,
        column: Int,
        weight: Double = 0.0,
        topInset: Int = 0,
        leftInset: Int = 0,
        bottomInset: Int = 0,
        rightInset: Int = 0,
        fill: Int = GridBagConstraints.NONE
    ): GridBagConstraints =
        GridBagConstraints().apply {
            gridx = column
            gridy = row
            weightx = weight
            this.fill = fill
            anchor = GridBagConstraints.WEST
            insets = JBUI.insets(topInset, leftInset, bottomInset, rightInset)
        }

    /**
     * Builds constraints for the flexible filler row.
     *
     * @return Grid bag constraints for filler components.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun fillerConstraints(): GridBagConstraints =
        GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        }
}
