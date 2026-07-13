package ms.shogun.devpack.ai

import java.io.File
import java.awt.BorderLayout

import javax.swing.Icon

import com.intellij.util.ui.JBUI
import com.intellij.util.IconUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBPanel
import com.intellij.openapi.project.Project
import com.intellij.ui.content.ContentFactory
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.wm.ex.ToolWindowManagerListener

import org.jetbrains.plugins.terminal.ShellStartupOptions
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.LocalTerminalDirectRunner

import ms.shogun.devpack.ShogunBundle.message

/**
 * Base tool-window factory for AI CLI integrations backed by a project-root terminal.
 *
 * @param definition AI terminal metadata used by the concrete tool window.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseAiTerminalToolWindowFactory(private val definition: AiTerminalDefinition) : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val rootPanel = JBPanel<JBPanel<*>>(BorderLayout()).withBorder(JBUI.Borders.empty(4))
        val terminalPanel = JBPanelWithEmptyText(BorderLayout())
        val state = AiTerminalSessionState(terminalPanel)

        terminalPanel.emptyText.text = emptyText()

        rootPanel.add(terminalPanel, BorderLayout.CENTER)

        val content = ContentFactory
            .getInstance()
            .createContent(rootPanel, project.name.ifBlank { definition.toolWindowId }, false)

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.setSelectedContent(content)

        toolWindow.setTitleActions(
            listOf(
                createReloadSessionAction(project, toolWindow.disposable, state),
                createCloseSessionAction(toolWindow, state),
            ),
        )
        toolWindow.setAdditionalGearActions(DefaultActionGroup())

        registerSessionStarter(project, toolWindow, state)

        if (!toolWindow.isVisible && !toolWindow.isActive) {
            return
        }

        ApplicationManager.getApplication().invokeLater {
            startSession(project, toolWindow.disposable, state)
            focusActiveSession(project, state)
        }
    }

    override suspend fun isApplicableAsync(project: Project): Boolean = definition.isEnabled()

    /**
     * Starts a new AI terminal session when no session is currently active.
     *
     * @param project Project used to resolve terminal startup options.
     * @param parentDisposable Parent disposable used to own the terminal session.
     * @param state Project tool-window state that owns the active session.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun startSession(project: Project, parentDisposable: Disposable, state: AiTerminalSessionState) {
        if (state.activeSession != null) {
            return
        }

        if (!definition.isEnabled()) {
            state.sessionPanel.emptyText.text = emptyText()

            return
        }

        val runner = LocalTerminalDirectRunner.createTerminalRunner(project)
        val startupOptions = ShellStartupOptions.Builder()
            .workingDirectory(projectRootPath(project))
            .shellCommand(definition.startupCommand(project))
            .envVariables(mapOf(PATH_ENVIRONMENT_VARIABLE to executableSearchPath()))
            .build()

        val sessionDisposable = Disposer.newDisposable("${definition.toolWindowId} terminal session")

        val widget = runCatching {
            ShellTerminalWidget.toShellJediTermWidgetOrThrow(
                runner.startShellTerminalWidget(sessionDisposable, startupOptions, false),
            )
        }.getOrElse { error ->
            Disposer.dispose(sessionDisposable)
            throw error
        }

        state.activeSession = widget
        state.activeSessionDisposable = sessionDisposable

        Disposer.register(parentDisposable, sessionDisposable)
        Disposer.register(sessionDisposable) {
            if (state.activeSession === widget) {
                state.activeSession = null
                state.activeSessionDisposable = null
            }
        }

        AiTerminalEscapeKeyForwarder.install(widget, sessionDisposable) {
            state.activeSession
        }

        state.sessionPanel.removeAll()
        state.sessionPanel.add(widget.component, BorderLayout.CENTER)

        state.sessionPanel.revalidate()
        state.sessionPanel.repaint()

        focusActiveSession(project, state)
    }

    /**
     * Registers a listener that starts a fresh session when the tool window is shown again.
     *
     * @param project Current project.
     * @param toolWindow AI terminal tool window.
     * @param state Project tool-window state that owns the active session.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun registerSessionStarter(project: Project, toolWindow: ToolWindow, state: AiTerminalSessionState) {
        project.messageBus
            .connect(toolWindow.disposable)
            .subscribe(
                ToolWindowManagerListener.TOPIC,
                object : ToolWindowManagerListener {
                    override fun toolWindowShown(shownToolWindow: ToolWindow) {
                        if (shownToolWindow.id == definition.toolWindowId) {
                            startSession(project, toolWindow.disposable, state)
                            focusActiveSession(project, state)
                        }
                    }
                },
            )
    }

    /**
     * Requests keyboard focus for the active terminal session.
     *
     * @param project Current project.
     * @param state Project tool-window state that owns the active session.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun focusActiveSession(project: Project, state: AiTerminalSessionState) {
        val focusableComponent = state.activeSession?.preferredFocusableComponent ?: return

        ApplicationManager.getApplication().invokeLater {
            IdeFocusManager.getInstance(project).requestFocus(focusableComponent, true)
        }
    }

    /**
     * Closes the active terminal session and hides the tool window.
     *
     * @param toolWindow AI terminal tool window.
     * @param state Project tool-window state that owns the active session.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun closeSession(toolWindow: ToolWindow, state: AiTerminalSessionState) {
        stopSession(state)
        resetSessionPanel(state)

        toolWindow.hide(null)
    }

    /**
     * Reloads the active terminal session without hiding the tool window.
     *
     * @param project Current project.
     * @param parentDisposable Parent disposable used to own the replacement terminal session.
     * @param state Project tool-window state that owns the active session.
     *
     * @author Almighty-Shogun
     * @since Unreleased
     */
    private fun reloadSession(project: Project, parentDisposable: Disposable, state: AiTerminalSessionState) {
        stopSession(state)
        resetSessionPanel(state)

        startSession(project, parentDisposable, state)
        focusActiveSession(project, state)
    }

    /**
     * Stops the active terminal widget and disposes the resources that own it.
     *
     * @param state Project tool-window state that owns the active session.
     *
     * @author Almighty-Shogun
     * @since Unreleased
     */
    private fun stopSession(state: AiTerminalSessionState) {
        state.activeSession?.close()
        state.activeSession = null
        state.activeSessionDisposable?.let { disposable ->
            Disposer.dispose(disposable)
        }
        state.activeSessionDisposable = null
    }

    /**
     * Clears the terminal panel and restores the empty-state message.
     *
     * @param state Project tool-window state that owns the terminal panel.
     *
     * @author Almighty-Shogun
     * @since Unreleased
     */
    private fun resetSessionPanel(state: AiTerminalSessionState) {
        state.sessionPanel.removeAll()
        state.sessionPanel.emptyText.text = emptyText()

        state.sessionPanel.revalidate()
        state.sessionPanel.repaint()
    }

    /**
     * Resolves the empty text shown before a terminal session exists.
     *
     * @return Disabled message or regular startup hint.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun emptyText(): String =
        if (definition.isEnabled()) {
            message(definition.emptyTextMessageKey)
        } else {
            message("ai.disabled.empty-text", definition.toolWindowId)
        }

    /**
     * Creates the title-bar action that reloads the current terminal session.
     *
     * @param project Current project.
     * @param parentDisposable Parent disposable used to own the replacement terminal session.
     * @param state Project tool-window state that owns the active session.
     *
     * @return Action displayed in the tool-window title bar.
     *
     * @author Almighty-Shogun
     * @since Unreleased
     */
    @Suppress("DialogTitleCapitalization")
    private fun createReloadSessionAction(
        project: Project,
        parentDisposable: Disposable,
        state: AiTerminalSessionState,
    ): DumbAwareAction =
        object : DumbAwareAction(message("ai.reload-session", definition.toolWindowId), null, reloadSessionIcon()) {
            override fun actionPerformed(event: AnActionEvent) {
                reloadSession(project, parentDisposable, state)
            }

            override fun update(event: AnActionEvent) {
                event.presentation.isEnabled = definition.isEnabled()
            }

            override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
        }

    /**
     * Creates the title-bar action that closes the current terminal session.
     *
     * @param toolWindow AI terminal tool window.
     * @param state Project tool-window state that owns the active session.
     *
     * @return Action displayed in the tool-window title bar.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun createCloseSessionAction(toolWindow: ToolWindow, state: AiTerminalSessionState): DumbAwareAction =
        object : DumbAwareAction(message(definition.closeSessionMessageKey), null, closeSessionIcon()) {
            override fun actionPerformed(event: AnActionEvent) {
                closeSession(toolWindow, state)
            }

            override fun update(event: AnActionEvent) {
                event.presentation.isEnabled = state.activeSession != null
            }

            override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
        }

    /**
     * Resolves the reload icon for the AI terminal title-bar action.
     *
     * @return Reload icon.
     *
     * @author Almighty-Shogun
     * @since Unreleased
     */
    private fun reloadSessionIcon(): Icon = AllIcons.Actions.Refresh

    /**
     * Creates a larger close icon for the AI terminal title-bar action.
     *
     * @return Scaled close icon.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun closeSessionIcon(): Icon = IconUtil.scale(AllIcons.Actions.Close, null, 1.35f)

    /**
     * Resolves the terminal working directory.
     *
     * @param project Current project.
     *
     * @return Project root path, or user home when the project path is unavailable.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun projectRootPath(project: Project): String = project.basePath ?: System.getProperty("user.home")

    /**
     * Builds a PATH value that includes common user-level package manager locations.
     *
     * @return Existing PATH with user binary directories prepended.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun executableSearchPath(): String =
        (userExecutableDirectories() + System.getenv(PATH_ENVIRONMENT_VARIABLE).orEmpty())
            .filter(String::isNotBlank)
            .distinct()
            .joinToString(File.pathSeparator)

    /**
     * Resolves user binary directories that desktop-launched IDE processes often miss.
     *
     * @return Existing user binary directories.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun userExecutableDirectories(): List<String> {
        val homeDirectory = System.getProperty("user.home")

        return listOf(
            "$homeDirectory/.bun/bin",
            "$homeDirectory/.local/bin",
            "$homeDirectory/.cargo/bin",
            "$homeDirectory/bin",
        ) + nvmNodeExecutableDirectories(homeDirectory)
    }

    /**
     * Resolves executable directories for Node versions managed by NVM.
     *
     * @param homeDirectory Current user home directory.
     *
     * @return Existing NVM Node executable directories.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun nvmNodeExecutableDirectories(homeDirectory: String): List<String> =
        File(homeDirectory, ".nvm/versions/node")
            .listFiles(File::isDirectory)
            .orEmpty()
            .map { nodeVersionDirectory -> File(nodeVersionDirectory, "bin").path }
            .filter { executableDirectory -> File(executableDirectory).isDirectory }

    /**
     * Mutable terminal state scoped to one project tool-window content instance.
     *
     * @property sessionPanel Panel that owns this terminal widget.
     * @property activeSession Terminal widget currently running in this content.
     * @property activeSessionDisposable Disposable that owns the current terminal widget.
     *
     * @author Almighty-Shogun
     * @since Unreleased
     */
    private class AiTerminalSessionState(val sessionPanel: JBPanelWithEmptyText) {
        var activeSession: ShellTerminalWidget? = null
        var activeSessionDisposable: Disposable? = null
    }

    private companion object {
        const val PATH_ENVIRONMENT_VARIABLE = "PATH"
    }
}
