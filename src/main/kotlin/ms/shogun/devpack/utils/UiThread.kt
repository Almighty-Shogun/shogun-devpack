package ms.shogun.devpack.utils

import com.intellij.openapi.application.ApplicationManager

/**
 * Schedules work on the IntelliJ UI thread.
 *
 * @param action Work to run on the UI thread.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
fun runOnUiThread(action: () -> Unit) = ApplicationManager.getApplication().invokeLater(action)

/**
 * Schedules UI work after a delay without blocking the UI thread.
 *
 * @param delayMillis Delay in milliseconds before scheduling the UI work.
 * @param action Work to run on the UI thread after the delay.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
fun runOnUiThreadAfterDelay(delayMillis: Long, action: () -> Unit) {
    ApplicationManager.getApplication().executeOnPooledThread {
        Thread.sleep(delayMillis)
        runOnUiThread(action)
    }
}
