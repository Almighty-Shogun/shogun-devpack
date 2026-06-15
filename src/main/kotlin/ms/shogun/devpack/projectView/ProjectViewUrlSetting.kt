package ms.shogun.devpack.projectView

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.registry.Registry

/**
 * Middle-man for JetBrains' built-in Project View URL visibility registry key.
 *
 * @author Almighty-Shogun
 * @since 1.1.0
 */
object ProjectViewUrlSetting {
    private const val SHOW_URL_REGISTRY_KEY = "project.tree.structure.show.url"

    /**
     * Checks whether the Project View URL/path text is currently hidden.
     *
     * @return `true` when Project View URL text is hidden.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    fun isHidden(): Boolean = !Registry.`is`(SHOW_URL_REGISTRY_KEY, true)

    /**
     * Updates JetBrains' Project View URL/path text registry setting.
     *
     * @param hidden Whether Project View URL text should be hidden.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    fun setHidden(hidden: Boolean) = Registry.get(SHOW_URL_REGISTRY_KEY).setValue(!hidden)

    /**
     * Refreshes every open Project View after changing the registry value.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun refreshOpenProjectViews() {
        ProjectManager.getInstance().openProjects.forEach { project ->
            ProjectView.getInstance(project).refresh()
        }
    }
}
