package ms.shogun.devpack.utils

import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.openapi.application.ApplicationManager

/**
 * Shared access helpers for plugin-owned IDE credential-store secrets.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CredentialStoreSecrets {
    /**
     * Reads a credential by internal key.
     *
     * @param key Internal credential key.
     *
     * @return Stored secret, or `null` when blank or unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun secret(key: String): String? =
        PasswordSafe.instance
            .getPassword(attributes(key))
            ?.takeIf { value ->
                value.isNotBlank()
            }

    /**
     * Reads a credential by internal key on a pooled thread.
     *
     * @param key Internal credential key.
     * @param callback Invoked on the application thread with the loaded secret.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun secretAsync(key: String, callback: (String?) -> Unit) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val value = secret(key)

            ApplicationManager.getApplication().invokeLater {
                callback(value)
            }
        }
    }

    /**
     * Writes a credential by internal key.
     *
     * @param key Internal credential key.
     * @param value Secret value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setSecret(key: String, value: String?) {
        PasswordSafe.instance[attributes(key)] = value
            ?.takeIf { secret ->
                secret.isNotBlank()
            }
            ?.let { secret ->
                Credentials("", secret)
            }
    }

    /**
     * Writes a credential by internal key on a pooled thread.
     *
     * @param key Internal credential key.
     * @param value Secret value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setSecretAsync(key: String, value: String?) {
        ApplicationManager.getApplication().executeOnPooledThread {
            setSecret(key, value)
        }
    }

    /**
     * Builds credential attributes for a plugin-owned secret.
     *
     * @param key Internal credential key.
     *
     * @return Credential store attributes.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun attributes(key: String): CredentialAttributes = CredentialAttributes("ShogunDevPack:$key")
}
