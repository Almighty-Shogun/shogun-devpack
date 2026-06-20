package ms.shogun.devpack.projectView

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Shared helpers for interacting with the Project View tool window.
 *
 * @author Almighty-Shogun
 * @since 1.1.0
 */
object ProjectViewToolWindow {
    private const val PROJECT_TOOL_WINDOW_ID = "Project"

    /**
     * Hides the Project View tool window when it is currently visible.
     *
     * @param project Current IntelliJ project.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    fun hideIfVisible(project: Project) {
        ToolWindowManager
            .getInstance(project)
            .getToolWindow(PROJECT_TOOL_WINDOW_ID)
            ?.takeIf { toolWindow ->
                toolWindow.isVisible
            }
            ?.hide()
    }
}
