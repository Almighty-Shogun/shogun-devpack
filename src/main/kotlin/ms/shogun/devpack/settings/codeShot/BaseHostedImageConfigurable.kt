package ms.shogun.devpack.settings.codeShot

import java.awt.GridBagLayout

import javax.swing.JPanel
import javax.swing.JComponent

import com.intellij.util.ui.JBUI
import com.intellij.ui.components.JBPasswordField
import com.intellij.openapi.options.SearchableConfigurable

import ms.shogun.devpack.settings.ui.SettingsUi
import ms.shogun.devpack.settings.ui.SettingsNotice

/**
 * Base settings page for hosted image providers that only require an API key.
 *
 * @param id Stable settings identifier.
 * @param texts Localized labels and descriptions used by the page.
 * @param apiKeyReader Reads the persisted API key asynchronously.
 * @param apiKeyWriter Stores or clears the API key.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseHostedImageConfigurable(
    private val id: String,
    private val texts: HostedImageSettingsTexts,
    private val apiKeyReader: ((String?) -> Unit) -> Unit,
    private val apiKeyWriter: (String?) -> Unit
) : SearchableConfigurable {
    private var loadedApiKey: String? = null
    private var apiKeyLoaded: Boolean = false
    private var apiKeyField: JBPasswordField? = null

    override fun getId(): String = id

    override fun getDisplayName(): String = texts.displayName

    override fun createComponent(): JComponent {
        apiKeyField = JBPasswordField()

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.emptyTop(8)

            add(SettingsNotice.info(texts.notice), SettingsUi.fullWidthConstraints(row = 0))
            add(SettingsUi.label(texts.apiKeyLabel), SettingsUi.fullWidthConstraints(row = 1, topInset = 14))
            add(requireNotNull(apiKeyField), SettingsUi.fullWidthConstraints(row = 2, topInset = 4))
            add(SettingsUi.description(texts.apiKeyDescription), SettingsUi.fullWidthConstraints(row = 3, topInset = 2))
            add(JPanel(), SettingsUi.fillerConstraints())
        }
    }

    override fun isModified(): Boolean {
        val currentApiKey = SettingsUi.normalizedPassword(apiKeyField)

        return if (apiKeyLoaded) {
            currentApiKey != loadedApiKey
        } else {
            currentApiKey != null
        }
    }

    override fun apply() {
        val currentApiKey = SettingsUi.normalizedPassword(apiKeyField)

        loadedApiKey = currentApiKey
        apiKeyLoaded = true
        apiKeyWriter(currentApiKey)
    }

    override fun reset() {
        loadedApiKey = null
        apiKeyLoaded = false
        apiKeyField?.text = ""
        apiKeyReader { apiKey ->
            loadedApiKey = apiKey
            apiKeyLoaded = true

            apiKeyField
                ?.takeIf { field ->
                    field.password.isEmpty()
                }
                ?.text = apiKey.orEmpty()
        }
    }

    override fun disposeUIResources() {
        apiKeyField = null
        loadedApiKey = null
        apiKeyLoaded = false
    }
}
