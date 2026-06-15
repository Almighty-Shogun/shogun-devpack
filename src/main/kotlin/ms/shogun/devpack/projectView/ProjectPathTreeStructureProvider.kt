package ms.shogun.devpack.projectView

import com.intellij.psi.PsiDirectory
import com.intellij.openapi.project.Project
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.impl.ProjectRootsUtil

import ms.shogun.devpack.settings.ShogunDevPackSettings

/**
 * Replaces only Project View root directory nodes with pathless variants.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class ProjectPathTreeStructureProvider : TreeStructureProvider {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): Collection<AbstractTreeNode<*>> =
        if (!ShogunDevPackSettings.instance.hideProjectViewPath) {
            children
        } else {
            children.map { child ->
                val directory = child.value as? PsiDirectory ?: return@map child

                if (shouldHidePath(parent, directory)) {
                    PathlessPsiDirectoryNode(directory.project, directory, settings)
                } else {
                    child
                }
            }
        }

    /**
     * Checks whether the directory is one of the nodes that normally shows the project path.
     *
     * @param parent Parent node currently being expanded.
     * @param directory Directory represented by the child node.
     *
     * @return `true` when the node should use a pathless presentation.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun shouldHidePath(parent: AbstractTreeNode<*>, directory: PsiDirectory): Boolean =
        parent.value is Project
            || ProjectRootsUtil.isProjectHome(directory)
            || ProjectRootsUtil.isModuleContentRoot(directory)
}
