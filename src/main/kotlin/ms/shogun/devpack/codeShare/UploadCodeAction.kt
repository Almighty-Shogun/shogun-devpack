package ms.shogun.devpack.codeShare

import java.awt.datatransfer.StringSelection

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.actionSystem.ActionUpdateThread

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.utils.PluginNotifications

/**
 * Uploads selected code or a selected project file to the configured sharing uploader.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class UploadCodeAction : AnAction() {
    private val notifications = PluginNotifications(message("code-share.notification.category"))

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.project ?: return
        val uploadSource = uploadSource(project, anActionEvent) ?: run {
            notifications.warning(project, message("code-share.notification.no-source"))
            return
        }

        object : Task.Backgroundable(project, message("code-share.progress"), true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = message("code-share.progress")

                try {
                    val result = CodeShareService.upload(uploadSource.request())

                    ApplicationManager.getApplication().invokeLater {
                        CopyPasteManager.getInstance().setContents(StringSelection(result.url))
                        notifications.info(project, message("code-share.notification.success", result.url))
                    }
                } catch (exception: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        notifications.error(
                            project,
                            message(
                                "code-share.notification.failure",
                                exception.message ?: exception::class.java.simpleName,
                            )
                        )
                    }
                }
            }
        }.queue()
    }

    override fun update(anActionEvent: AnActionEvent) {
        val project = anActionEvent.project
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val virtualFile = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE)

        val hasEditorSelection = editor != null && editor.selectionModel.hasSelection()
        val hasUploadableFile = uploadableFile(virtualFile)

        anActionEvent.presentation.text = if (hasEditorSelection) {
            message("action.ShogunDevPack.CodeShare.Upload.snippet.text")
        } else {
            message("action.ShogunDevPack.CodeShare.Upload.file.text")
        }

        anActionEvent.presentation.isEnabled = project != null && (hasEditorSelection || hasUploadableFile)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    /**
     * Resolves the source that should be uploaded.
     *
     * @param project Current project.
     * @param anActionEvent Action event containing editor or file context.
     *
     * @return Upload source, or `null` when nothing uploadable is selected.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploadSource(project: Project, anActionEvent: AnActionEvent): UploadSource? {
        val eventEditor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val eventVirtualFile = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE)

        editorSelectionUploadSource(eventEditor, eventVirtualFile)?.let { uploadSource ->
            return uploadSource
        }

        if (eventEditor == null && uploadableFile(eventVirtualFile)) {
            return FileUploadSource(requireNotNull(eventVirtualFile))
        }

        val selectedEditor = FileEditorManager.getInstance(project).selectedTextEditor

        editorSelectionUploadSource(selectedEditor, eventVirtualFile)?.let { uploadSource ->
            return uploadSource
        }

        return if (uploadableFile(eventVirtualFile)) {
            FileUploadSource(requireNotNull(eventVirtualFile))
        } else {
            null
        }
    }

    /**
     * Builds an upload source from an editor selection.
     *
     * @param editor Editor that may contain selected code.
     * @param fallbackVirtualFile Context file used when the editor document has no virtual file.
     *
     * @return Editor selection upload source, or `null` when no selection exists.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun editorSelectionUploadSource(editor: Editor?, fallbackVirtualFile: VirtualFile?): UploadSource? {
        val selectionModel = editor?.selectionModel

        if (selectionModel?.hasSelection() != true) {
            return null
        }

        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: fallbackVirtualFile

        return EditorSelectionUploadSource(
            fileName = virtualFile?.name ?: "selection.txt",
            content = selectionModel.selectedText.orEmpty(),
            syntaxName = virtualFile?.extension
        )
    }

    /**
     * Checks whether a virtual file can be uploaded.
     *
     * @param virtualFile Candidate virtual file.
     *
     * @return `true` when the file exists and is not a directory.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploadableFile(virtualFile: VirtualFile?): Boolean =
        virtualFile != null && virtualFile.isValid && !virtualFile.isDirectory

    /**
     * Upload source that can produce a uploader request.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private interface UploadSource {
        fun request(): CodeShareRequest
    }

    /**
     * Upload source backed by the current editor selection.
     *
     * @property fileName File name used by the sharing uploader.
     * @property content Selected editor text.
     * @property syntaxName Optional syntax name derived from the file extension.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private data class EditorSelectionUploadSource(
        private val fileName: String,
        private val content: String,
        private val syntaxName: String?
    ) : UploadSource {
        override fun request(): CodeShareRequest =
            CodeShareRequest(
                fileName = fileName,
                content = content,
                syntaxName = syntaxName,
            )
    }

    /**
     * Upload source backed by a project file.
     *
     * @property virtualFile File selected in the Project View.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private data class FileUploadSource(private val virtualFile: VirtualFile) : UploadSource {
        override fun request(): CodeShareRequest {
            if (virtualFile.length > MAX_UPLOAD_BYTES) {
                throw CodeShareException(message("code-share.notification.file-too-large"))
            }

            val content = runCatching {
                String(virtualFile.contentsToByteArray(), virtualFile.charset)
            }.getOrElse { exception ->
                throw CodeShareException(exception.message ?: message("code-share.notification.file-read-failed"))
            }

            return CodeShareRequest(
                fileName = virtualFile.name,
                content = content,
                syntaxName = virtualFile.extension,
            )
        }
    }

    companion object {
        private const val MAX_UPLOAD_BYTES = 1_000_000L
    }
}
