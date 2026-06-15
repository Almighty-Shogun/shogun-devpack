package ms.shogun.devpack.utils

import com.intellij.openapi.project.Project
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationGroupManager

import ms.shogun.devpack.PluginIcons
import ms.shogun.devpack.ShogunBundle.message

/**
 * Small wrapper around IntelliJ notifications with plugin branding.
 *
 * @param category Category shown in the notification title.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class PluginNotifications(private val category: String) {
    /**
     * Shows an informational notification and expires it after a short delay.
     *
     * @param project Project that should receive the notification, or `null` for application-level notification.
     * @param message Notification body text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun info(project: Project?, message: String) = show(project, NotificationType.INFORMATION, message)

    /**
     * Shows a warning notification and expires it after a short delay.
     *
     * @param project Project that should receive the notification, or `null` for application-level notification.
     * @param message Notification body text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun warning(project: Project?, message: String) = show(project, NotificationType.WARNING, message)

    /**
     * Shows an error notification and expires it after a short delay.
     *
     * @param project Project that should receive the notification, or `null` for application-level notification.
     * @param message Notification body text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun error(project: Project?, message: String) = show(project, NotificationType.ERROR, message)

    /**
     * Creates a notification with the requested severity.
     *
     * @param project Project that should receive the notification, or `null` for application-level notification.
     * @param notificationType IntelliJ notification severity.
     * @param message Notification body text.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun show(project: Project?, notificationType: NotificationType, message: String) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(message("notification.group.id"))
            .createNotification(message("notification.title", category), message, notificationType)
            .setIcon(PluginIcons.brandingSmall)

        notification.notify(project)

        runOnUiThreadAfterDelay(10000L) {
            notification.expire()
        }
    }
}
