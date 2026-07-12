package ms.shogun.devpack.ai

import java.awt.Component
import java.awt.event.KeyEvent
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager

import javax.swing.SwingUtilities

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer

import org.jetbrains.plugins.terminal.ShellTerminalWidget

/**
 * Forwards Escape to the active AI terminal before the IDE can treat it as editor focus navigation.
 *
 * @author Almighty-Shogun
 * @since 1.3.0
 */
internal object AiTerminalEscapeKeyForwarder {
    private const val ESCAPE_CHARACTER = "\u001B"

    /**
     * Installs an Escape key dispatcher for the lifetime of a terminal session.
     *
     * @param widget Terminal widget that should receive Escape.
     * @param sessionDisposable Disposable that removes the dispatcher when the session ends.
     * @param activeSession Resolver for the currently active AI terminal session.
     *
     * @author Almighty-Shogun
     * @since 1.3.0
     */
    fun install(widget: ShellTerminalWidget, sessionDisposable: Disposable, activeSession: () -> ShellTerminalWidget?) {
        val component = widget.component
        val focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager()

        val dispatcher = KeyEventDispatcher { event ->
            if (!shouldForward(event, widget, component, focusManager, activeSession)) {
                return@KeyEventDispatcher false
            }

            sendEscape(widget)
            true
        }

        focusManager.addKeyEventDispatcher(dispatcher)

        Disposer.register(sessionDisposable) {
            focusManager.removeKeyEventDispatcher(dispatcher)
        }
    }

    /**
     * Checks whether a key event should be intercepted and sent to this terminal.
     *
     * @param event Key event to inspect.
     * @param widget Terminal widget that installed the dispatcher.
     * @param component Root Swing component for the terminal.
     * @param focusManager Focus manager used to verify terminal focus ownership.
     * @param activeSession Resolver for the currently active AI terminal session.
     *
     * @return Whether Escape should be forwarded to the terminal process.
     *
     * @author Almighty-Shogun
     * @since 1.3.0
     */
    private fun shouldForward(
        event: KeyEvent,
        widget: ShellTerminalWidget,
        component: Component,
        focusManager: KeyboardFocusManager,
        activeSession: () -> ShellTerminalWidget?
    ): Boolean =
        event.isPlainEscapePress() &&
            activeSession() === widget &&
            focusManager.hasFocusInside(component)

    /**
     * Checks whether focus is currently inside the terminal component hierarchy.
     *
     * @param component Root Swing component for the terminal.
     *
     * @return Whether the current focus owner is the terminal or one of its children.
     *
     * @author Almighty-Shogun
     * @since 1.3.0
     */
    private fun KeyboardFocusManager.hasFocusInside(component: Component): Boolean {
        val focusOwner = focusOwner ?: return false

        return focusOwner === component || SwingUtilities.isDescendingFrom(focusOwner, component)
    }

    /**
     * Checks whether the event is an unmodified Escape key press.
     *
     * @return Whether the key event represents plain Escape.
     *
     * @author Almighty-Shogun
     * @since 1.3.0
     */
    private fun KeyEvent.isPlainEscapePress(): Boolean =
        id == KeyEvent.KEY_PRESSED &&
            keyCode == KeyEvent.VK_ESCAPE &&
            modifiersEx == 0 &&
            !isConsumed

    /**
     * Writes a raw Escape character to the terminal process.
     *
     * @param widget Terminal widget receiving the Escape character.
     *
     * @author Almighty-Shogun
     * @since 1.3.0
     */
    private fun sendEscape(widget: ShellTerminalWidget) {
        runCatching {
            widget.executeWithTtyConnector { ttyConnector ->
                if (ttyConnector.isConnected) {
                    ttyConnector.write(ESCAPE_CHARACTER)
                }
            }
        }
    }
}
