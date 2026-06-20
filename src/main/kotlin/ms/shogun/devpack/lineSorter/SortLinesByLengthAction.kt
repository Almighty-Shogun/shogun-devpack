package ms.shogun.devpack.lineSorter

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ActionUpdateThread

import ms.shogun.devpack.ShogunBundle.message

/**
 * Sorts the selected editor lines by their line length.
 *
 * @author Almighty-Shogun
 * @since 1.1.0
 */
class SortLinesByLengthAction : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return

        val selectionModel = editor.selectionModel

        if (!selectionModel.hasSelection()) {
            return
        }

        val selectedText = selectionModel.selectedText ?: return
        val formattedText = SortLinesByLengthFormatter.format(selectedText)

        val selectionStart = selectionModel.selectionStart
        val selectionEnd = selectionModel.selectionEnd

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(selectionStart, selectionEnd, formattedText)
            selectionModel.setSelection(selectionStart, selectionStart + formattedText.length)
            PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        }
    }

    override fun update(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)

        event.presentation.text = message("action.ShogunDevPack.SortLinesByLength.text")
        event.presentation.isEnabled = editor?.hasSelection() == true
    }

    /**
     * Checks if the editor has selected text.
     *
     * @return `true` when selected text exists.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun Editor.hasSelection(): Boolean = selectionModel.hasSelection()
}
