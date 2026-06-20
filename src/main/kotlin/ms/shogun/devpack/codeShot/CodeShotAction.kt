package ms.shogun.devpack.codeShot

import java.awt.image.BufferedImage
import java.awt.datatransfer.StringSelection

import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.actionSystem.ActionUpdateThread

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.utils.PluginNotifications
import ms.shogun.devpack.settings.ShogunDevPackSettings
import ms.shogun.devpack.codeShot.uploaders.ImgBbUploader
import ms.shogun.devpack.codeShot.uploaders.FreeimageHostUploader
import ms.shogun.devpack.codeShot.uploaders.CodeShotCustomServerUploader

/**
 * Renders the currently selected editor lines as an image and sends it to the configured output target.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShotAction : AnAction() {
    private val notifications = PluginNotifications(message("code-shot.notification.category"))

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.project ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

        val selectionModel = editor.selectionModel

        if (selectionModel.blockSelectionStarts.size >= 2) {
            notifications.warning(project, message("code-shot.notification.multiple-selections"))
            return
        }

        val end = selectionModel.selectionEnd
        val start = selectionModel.selectionStart

        val picture = try {
            selectionModel.removeSelection()

            val fragment = CodeFragment.createCodeFragmentComponent(editor, start, end)

            CodeScreenshotRenderer.render(fragment)
        } finally {
            selectionModel.setSelection(start, end)
        }

        when (CodeShotOutputTarget.from(ShogunDevPackSettings.instance.codeShotOutputTarget)) {
            CodeShotOutputTarget.CLIPBOARD -> copyImageToClipboard(project, picture)
            CodeShotOutputTarget.FREEIMAGE_HOST -> uploadToFreeimageHost(project, picture)
            CodeShotOutputTarget.IMGBB -> uploadToImgBb(project, picture)
            CodeShotOutputTarget.CUSTOM_SERVER -> uploadToCustomServer(project, picture)
        }
    }

    override fun update(anActionEvent: AnActionEvent) {
        val project = anActionEvent.getData(LangDataKeys.PROJECT)
        val editor = anActionEvent.getData(LangDataKeys.EDITOR)
        val outputTarget = CodeShotOutputTarget.from(ShogunDevPackSettings.instance.codeShotOutputTarget)

        anActionEvent.presentation.text = if (outputTarget == CodeShotOutputTarget.CLIPBOARD) {
            message("action.ShogunDevPack.CodeShot.text")
        } else {
            message("action.ShogunDevPack.CodeShot.upload.text")
        }

        anActionEvent.presentation.isEnabled = project != null && editor != null && editor.selectionModel.hasSelection()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    /**
     * Copies the rendered screenshot image to the clipboard.
     *
     * @param project Current project.
     * @param picture Rendered screenshot image.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun copyImageToClipboard(project: Project, picture: BufferedImage) {
        CopyPasteManager.getInstance().setContents(ImageTransferable(picture))
        notifications.info(project, message("code-shot.notification.success"))
    }

    /**
     * Uploads the rendered screenshot to Freeimage.host.
     *
     * @param project Current project.
     * @param picture Rendered screenshot image.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploadToFreeimageHost(project: Project, picture: BufferedImage) {
        uploadImage(
            project = project,
            picture = picture,
            progressMessage = message("code-shot.progress.freeimage-host"),
            successMessageKey = "code-shot.notification.freeimage-host.success",
            failureMessageKey = "code-shot.notification.freeimage-host.failure",
            uploader = FreeimageHostUploader::upload,
        )
    }

    /**
     * Uploads the rendered screenshot to ImgBB.
     *
     * @param project Current project.
     * @param picture Rendered screenshot image.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploadToImgBb(project: Project, picture: BufferedImage) {
        uploadImage(
            project = project,
            picture = picture,
            progressMessage = message("code-shot.progress.imgbb"),
            successMessageKey = "code-shot.notification.imgbb.success",
            failureMessageKey = "code-shot.notification.imgbb.failure",
            uploader = ImgBbUploader::upload,
        )
    }

    /**
     * Uploads the rendered screenshot to the configured custom server.
     *
     * @param project Current project.
     * @param picture Rendered screenshot image.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploadToCustomServer(project: Project, picture: BufferedImage) {
        uploadImage(
            project = project,
            picture = picture,
            progressMessage = message("code-shot.progress.custom-server"),
            successMessageKey = "code-shot.notification.custom-server.success",
            failureMessageKey = "code-shot.notification.custom-server.failure",
            uploader = CodeShotCustomServerUploader::upload,
        )
    }

    /**
     * Runs a screenshot upload on a background thread and copies the returned URL.
     *
     * @param project Current project.
     * @param picture Rendered screenshot image.
     * @param progressMessage Progress indicator text.
     * @param successMessageKey Resource bundle key for the success notification.
     * @param failureMessageKey Resource bundle key for the failure notification.
     * @param uploader Upload function that receives PNG bytes and returns the copied URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun uploadImage(
        project: Project,
        picture: BufferedImage,
        progressMessage: String,
        successMessageKey: String,
        failureMessageKey: String,
        uploader: (ByteArray) -> String
    ) {
        object : Task.Backgroundable(project, progressMessage, true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = progressMessage

                try {
                    val url = uploader(CodeShotImageEncoder.pngBytes(picture))

                    ApplicationManager.getApplication().invokeLater {
                        CopyPasteManager.getInstance().setContents(StringSelection(url))
                        notifications.info(project, message(successMessageKey, url))
                    }
                } catch (exception: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        notifications.error(
                            project,
                            message(
                                failureMessageKey,
                                exception.message ?: exception::class.java.simpleName,
                            )
                        )
                    }
                }
            }
        }.queue()
    }
}
