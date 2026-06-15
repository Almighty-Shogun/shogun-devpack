package ms.shogun.devpack.settings.customServer

import java.awt.BorderLayout
import java.awt.GridBagLayout
import java.awt.GridBagConstraints

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.JBTabbedPane
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.utils.customServer.HttpMethod
import ms.shogun.devpack.settings.ui.KeyValueSettingsPanel

/**
 * Shared settings page for custom server upload integrations.
 *
 * @param id Stable settings identifier.
 * @param texts Localized labels and descriptions used by the page.
 * @param readState Reads persisted custom server settings.
 * @param writeState Persists custom server settings.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseCustomServerConfigurable(
    private val id: String,
    private val texts: CustomServerSettingsTexts,
    private val readState: () -> CustomServerSettingsState,
    private val writeState: (CustomServerSettingsState) -> Unit
) : SearchableConfigurable {
    private var requestUrlTextField: JBTextField? = null
    private var urlTemplateTextField: JBTextField? = null
    private var fileFormNameTextField: JBTextField? = null
    private var methodComboBox: ComboBox<HttpMethod>? = null
    private var requestBodyTable: KeyValueSettingsPanel? = null
    private var urlParametersTable: KeyValueSettingsPanel? = null
    private var requestHeadersTable: KeyValueSettingsPanel? = null

    override fun getId(): String = id

    override fun getDisplayName(): String = message("settings.custom-server.display-name")

    override fun createComponent(): JComponent {
        methodComboBox = ComboBox(HttpMethod.entries.toTypedArray())
        requestUrlTextField = JBTextField()
        requestBodyTable = keyValueTable()
        urlParametersTable = keyValueTable()
        requestHeadersTable = keyValueTable()
        fileFormNameTextField = JBTextField().apply {
            columns = 16
        }
        urlTemplateTextField = JBTextField()

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(requestTargetPanel(), SettingsUi.fullWidthConstraints(row = 0))
            add(SettingsUi.sectionSeparator(texts.requestSettings), SettingsUi.fullWidthConstraints(row = 1, topInset = 16))
            add(requestSettingsTabs(), SettingsUi.fullWidthConstraints(row = 2, topInset = 10))
            add(SettingsUi.sectionSeparator(texts.responseSettings), SettingsUi.fullWidthConstraints(row = 3, topInset = 6))
            add(SettingsUi.label(texts.urlTemplate), SettingsUi.fullWidthConstraints(row = 4, topInset = 6))
            add(requireNotNull(urlTemplateTextField), SettingsUi.fullWidthConstraints(row = 5, topInset = 4))
            add(SettingsUi.description(texts.urlTemplateDescription), SettingsUi.fullWidthConstraints(row = 6, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean =
        currentState() != readState()

    override fun apply() {
        writeState(currentState())
    }

    override fun reset() {
        val state = readState()

        methodComboBox?.selectedItem = HttpMethod.from(state.method)
        requestUrlTextField?.text = state.url.orEmpty()
        requestBodyTable?.setSerializedValue(state.requestBody)
        urlParametersTable?.setSerializedValue(state.urlParameters)
        requestHeadersTable?.setSerializedValue(state.requestHeaders)
        fileFormNameTextField?.text = state.fileFormName
        urlTemplateTextField?.text = state.urlTemplate
    }

    override fun disposeUIResources() {
        methodComboBox = null
        requestUrlTextField = null
        requestBodyTable = null
        urlParametersTable = null
        requestHeadersTable = null
        fileFormNameTextField = null
        urlTemplateTextField = null
    }

    /**
     * Builds the current settings state from UI controls.
     *
     * @return Current custom server settings state.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun currentState(): CustomServerSettingsState =
        CustomServerSettingsState(
            method = selectedMethod().name,
            url = SettingsUi.normalizedText(requestUrlTextField),
            requestBody = requestBodyTable?.serializedValue(),
            urlParameters = urlParametersTable?.serializedValue(),
            requestHeaders = requestHeadersTable?.serializedValue(),
            fileFormName = SettingsUi.nonBlankText(fileFormNameTextField, DEFAULT_FILE_FORM_NAME),
            urlTemplate = SettingsUi.nonBlankText(urlTemplateTextField, DEFAULT_URL_TEMPLATE),
        )

    /**
     * Returns the selected HTTP method.
     *
     * @return Selected method, or POST when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun selectedMethod(): HttpMethod =
        methodComboBox?.selectedItem as? HttpMethod ?: HttpMethod.POST

    /**
     * Creates the HTTP method and request URL row.
     *
     * @return Request target panel.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun requestTargetPanel(): JPanel =
        JPanel(GridBagLayout()).apply {
            add(SettingsUi.label(texts.method), targetLabelConstraints(column = 0))
            add(SettingsUi.label(texts.requestUrl), targetLabelConstraints(column = 1, leftInset = 12))
            add(SettingsUi.label(texts.fileFormName), targetLabelConstraints(column = 2, leftInset = 12))
            add(requireNotNull(methodComboBox), targetFieldConstraints(column = 0, weight = 0.0))
            add(requireNotNull(requestUrlTextField), targetFieldConstraints(column = 1, weight = 1.0, leftInset = 12))
            add(requireNotNull(fileFormNameTextField), targetFieldConstraints(column = 2, weight = 0.0, leftInset = 12))
            add(SettingsUi.description(texts.requestUrlDescription), targetDescriptionConstraints(column = 1))
            add(SettingsUi.description(texts.fileFormNameDescription), targetDescriptionConstraints(column = 2))
        }

    /**
     * Creates a key/value table for request settings.
     *
     * @return Key/value table panel.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun keyValueTable(): KeyValueSettingsPanel =
        KeyValueSettingsPanel(
            keyColumnName = texts.key,
            valueColumnName = texts.value,
        )

    /**
     * Creates tabs for optional request key/value settings.
     *
     * @return Tabbed request settings component.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun requestSettingsTabs(): JBTabbedPane =
        JBTabbedPane().apply {
            addTab(
                texts.urlParameters,
                tabPanel(
                    texts.urlParametersDescription,
                    requireNotNull(urlParametersTable),
                ),
            )
            addTab(
                texts.requestHeaders,
                tabPanel(
                    texts.requestHeadersDescription,
                    requireNotNull(requestHeadersTable),
                ),
            )
            addTab(
                texts.requestBody,
                tabPanel(
                    texts.requestBodyDescription,
                    requireNotNull(requestBodyTable),
                ),
            )
        }

    /**
     * Creates a tab containing a description and editable key/value table.
     *
     * @param description Tab description.
     * @param table Key/value settings table.
     *
     * @return Tab content panel.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun tabPanel(description: String, table: KeyValueSettingsPanel): JPanel =
        JPanel(BorderLayout(0, JBUI.scale(8))).apply {
            border = JBUI.Borders.empty(6, 0)

            add(SettingsUi.description(description), BorderLayout.NORTH)
            add(table, BorderLayout.CENTER)
        }

    /**
     * Builds constraints for labels inside the request target row.
     *
     * @param column Grid column.
     * @param leftInset Left spacing.
     *
     * @return Grid bag constraints for labels.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun targetLabelConstraints(column: Int, leftInset: Int = 0): GridBagConstraints =
        SettingsUi.cellConstraints(row = 0, column = column, leftInset = leftInset)

    /**
     * Builds constraints for fields inside the request target row.
     *
     * @param column Grid column.
     * @param weight Horizontal weight.
     * @param leftInset Left spacing.
     *
     * @return Grid bag constraints for fields.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun targetFieldConstraints(column: Int, weight: Double, leftInset: Int = 0): GridBagConstraints =
        SettingsUi.cellConstraints(
            row = 1,
            column = column,
            weight = weight,
            topInset = 4,
            leftInset = leftInset,
            fill = GridBagConstraints.HORIZONTAL
        )

    /**
     * Builds constraints for descriptions inside the request target row.
     *
     * @param column Grid column.
     * @return Grid bag constraints for descriptions.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun targetDescriptionConstraints(column: Int): GridBagConstraints =
        SettingsUi.cellConstraints(
            row = 2,
            column = column,
            weight = if (column == 1) 1.0 else 0.0,
            topInset = 2,
            leftInset = 12,
            fill = GridBagConstraints.HORIZONTAL
        )

    companion object {
        private const val DEFAULT_FILE_FORM_NAME = "file"
        private const val DEFAULT_URL_TEMPLATE = "{response}"
    }
}
