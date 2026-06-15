package ms.shogun.devpack.settings.ui

import java.awt.Dimension
import java.awt.BorderLayout

import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

import com.intellij.util.ui.JBUI
import com.intellij.ui.table.JBTable
import com.intellij.ui.ToolbarDecorator
import com.intellij.openapi.actionSystem.ActionToolbarPosition

/**
 * Two-column settings editor for string key/value pairs.
 *
 * @param keyColumnName Visible name for the key column.
 * @param valueColumnName Visible name for the value column.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class KeyValueSettingsPanel(keyColumnName: String, valueColumnName: String) : JPanel(BorderLayout()) {
    private val tableModel = object : DefaultTableModel(arrayOf(keyColumnName, valueColumnName), 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = true
    }

    private val table = JBTable(tableModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        rowHeight = JBUI.scale(24)
        emptyText.text = ""
    }

    init {
        add(
            ToolbarDecorator.createDecorator(table)
                .setToolbarPosition(ActionToolbarPosition.TOP)
                .disableUpDownActions()
                .setVisibleRowCount(2)
                .setAddAction {
                    addRow()
                }
                .setEditAction {
                    editSelectedRow()
                }
                .setRemoveAction {
                    removeSelectedRow()
                }
                .setPreferredSize(Dimension(0, JBUI.scale(88)))
                .createPanel(),
            BorderLayout.CENTER,
        )
    }

    /**
     * Replaces the current table contents.
     *
     * @param value Persisted line-based key/value string.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setSerializedValue(value: String?) {
        tableModel.rowCount = 0

        parse(value).forEach { (key, fieldValue) ->
            tableModel.addRow(arrayOf(key, fieldValue))
        }
    }

    /**
     * Serializes the current table contents into the persisted line-based format.
     *
     * @return Serialized key/value pairs, or `null` when no complete rows exist.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun serializedValue(): String? =
        entries()
            .takeIf { value ->
                value.isNotEmpty()
            }
            ?.joinToString("\n") { (key, value) ->
                "$key=$value"
            }

    /**
     * Returns all complete key/value rows.
     *
     * @return Non-blank key/value pairs.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun entries(): List<Pair<String, String>> =
        (0 until tableModel.rowCount)
            .mapNotNull { row ->
                val key = tableModel.getValueAt(row, KEY_COLUMN)?.toString()?.trim().orEmpty()
                val value = tableModel.getValueAt(row, VALUE_COLUMN)?.toString()?.trim().orEmpty()

                if (key.isBlank() || value.isBlank()) {
                    null
                } else {
                    key to value
                }
            }

    /**
     * Adds a new editable row.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun addRow() {
        tableModel.addRow(arrayOf("", ""))

        val row = tableModel.rowCount - 1

        table.setRowSelectionInterval(row, row)
        table.editCellAt(row, KEY_COLUMN)
        table.requestFocusInWindow()
    }

    /**
     * Starts editing the selected row.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun editSelectedRow() {
        val selectedRow = table.selectedRow.takeIf { row ->
            row >= 0
        } ?: return

        table.editCellAt(selectedRow, KEY_COLUMN)
        table.requestFocusInWindow()
    }

    /**
     * Removes the selected row.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun removeSelectedRow() {
        val selectedRow = table.selectedRow.takeIf { row ->
            row >= 0
        } ?: return

        tableModel.removeRow(table.convertRowIndexToModel(selectedRow))
    }

    companion object {
        private const val KEY_COLUMN = 0
        private const val VALUE_COLUMN = 1

        /**
         * Parses a line-based key/value setting.
         *
         * @param value Persisted value.
         *
         * @return Parsed pairs.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        fun parse(value: String?): List<Pair<String, String>> =
            value
                .orEmpty()
                .lineSequence()
                .map { line ->
                    line.trim()
                }
                .filter { line ->
                    line.isNotBlank() && !line.startsWith("#")
                }
                .mapNotNull { line ->
                    val separatorIndex = separatorIndex(line)

                    if (separatorIndex <= 0) {
                        null
                    } else {
                        line.substring(0, separatorIndex).trim() to line.substring(separatorIndex + 1).trim()
                    }
                }
                .toList()

        /**
         * Finds the first supported key/value separator.
         *
         * @param line Persisted setting line.
         *
         * @return Separator index, or `-1` when absent.
         *
         * @author Almighty-Shogun
         * @since 1.0.0
         */
        private fun separatorIndex(line: String): Int {
            val equalsIndex = line.indexOf('=')
            val colonIndex = line.indexOf(':')

            return listOf(equalsIndex, colonIndex)
                .filter { index ->
                    index >= 0
                }
                .minOrNull() ?: -1
        }
    }
}
