package ms.shogun.devpack.shiftTab

import javax.swing.SwingConstants

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx

/**
 * Moves the active editor tab between split panes.
 *
 * @param orientation Direction in which the active tab should move.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class ShiftTabAction(private val orientation: TabOrientation) : DumbAwareAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.project ?: return
        val file = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val fileEditorManager = FileEditorManagerEx.getInstanceEx(project)
        val windowManager = ToolWindowManager.getInstance(project)
        val projectWindow = ToolWindowManager.getInstance(project).getToolWindow("Project") ?: return

        if (projectWindow.isVisible) projectWindow.hide()

        if (!windowManager.isEditorComponentActive) return

        val index = this.getCurrentSplitIndex(fileEditorManager)

        if (this.orientation == TabOrientation.RIGHT || this.orientation == TabOrientation.DOWN) {
            val isLastSplit = (fileEditorManager.windows.size - 1 == index)

            if (isLastSplit && (fileEditorManager.currentWindow?.tabCount ?: 0) == 1) return

            if (isLastSplit) {
                val alignment = if (this.orientation == TabOrientation.RIGHT) SwingConstants.VERTICAL else SwingConstants.HORIZONTAL

                fileEditorManager.createSplitter(alignment, fileEditorManager.currentWindow)
                fileEditorManager.windows[index].closeFile(file)
                fileEditorManager.windows[index + 1].setAsCurrentWindow(true)
            } else {
                val wasOnlyTab = fileEditorManager.windows[index].fileList.size == 1
                val shift = if (wasOnlyTab) 0 else 1

                fileEditorManager.windows[index].closeFile(file)
                fileEditorManager.windows[index + shift].setAsCurrentWindow(true)
            }
        } else {
            val isFirstSplit = (index == 0)

            if (isFirstSplit && (fileEditorManager.currentWindow?.tabCount ?: 0) == 1) return

            var newIndex = index - 1

            if (newIndex < 0) {
                newIndex = fileEditorManager.windows.size - 1
            }

            fileEditorManager.windows[index].closeFile(file)
            fileEditorManager.windows[newIndex].setAsCurrentWindow(true)
        }

        focusFile(project, fileEditorManager, file)
    }

    /**
     * Finds the index of the active editor split.
     *
     * @param fileEditorManagerEx Editor manager used to inspect split windows.
     *
     * @return Index of the current editor split, or `-1` when no split is active.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun getCurrentSplitIndex(fileEditorManagerEx: FileEditorManagerEx): Int = fileEditorManagerEx.windows.indexOfFirst { it == fileEditorManagerEx.currentWindow }

    /**
     * Reopens the moved file in the target split and restores editor keyboard focus.
     *
     * @param project Current IntelliJ project.
     * @param fileEditorManager Editor manager that owns the target split.
     * @param file File that should remain active after moving.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun focusFile(project: Project, fileEditorManager: FileEditorManager, file: VirtualFile) {
        fileEditorManager.openFile(file, true)

        ApplicationManager.getApplication().invokeLater {
            val editor = fileEditorManager.selectedTextEditor ?: return@invokeLater

            IdeFocusManager.getInstance(project).requestFocus(editor.contentComponent, true)
        }
    }
}
