package ms.shogun.devpack

import com.intellij.util.IconUtil
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

import ms.shogun.devpack.utils.runOnUiThread
import ms.shogun.devpack.ShogunBundle.message

/**
 * Shows basic plugin information from the main Shogun Utilities menu.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class AboutAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        runOnUiThread {
            Messages.showMessageDialog(
                event.project,
                message("about.message"),
                message("plugin.name"),
                IconUtil.scale(PluginIcons.branding, null, 2f),
            )
        }
    }
}
