package ms.shogun.devpack.ai

import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread

/**
 * Base action for toggling AI terminal tool windows.
 *
 * @param definition AI terminal metadata used to find the target tool window.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseToggleAiTerminalToolWindowAction(private val definition: AiTerminalDefinition) : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(event: AnActionEvent) {
        if (!definition.isEnabled()) {
            return
        }

        val project = event.project ?: return
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(definition.toolWindowId) ?: return

        if (toolWindow.isVisible) {
            toolWindow.hide(null)
        } else {
            toolWindow.activate(null, true)
        }
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = event.project != null && definition.isEnabled()
    }
}
