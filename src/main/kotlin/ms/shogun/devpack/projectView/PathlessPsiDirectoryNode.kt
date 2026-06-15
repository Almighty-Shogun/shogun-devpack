package ms.shogun.devpack.projectView

import com.intellij.psi.PsiDirectory
import com.intellij.openapi.project.Project
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode

/**
 * Project View directory node that hides IntelliJ's secondary location text.
 *
 * @param project Project that owns the directory.
 * @param directory Directory represented by the node.
 * @param viewSettings Active Project View settings.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
internal class PathlessPsiDirectoryNode(
    project: Project,
    directory: PsiDirectory,
    viewSettings: ViewSettings
) : PsiDirectoryNode(project, directory, viewSettings) {
    override fun updateImpl(data: PresentationData) {
        super.updateImpl(data)

        data.locationString = null
    }
}
