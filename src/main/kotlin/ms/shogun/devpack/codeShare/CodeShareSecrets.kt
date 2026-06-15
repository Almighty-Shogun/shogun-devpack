package ms.shogun.devpack.codeShare

import ms.shogun.devpack.utils.CredentialStoreSecrets

/**
 * Stores sensitive code sharing credentials in the IDE credential store.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodeShareSecrets {
    private const val GITHUB_TOKEN_KEY = "code-share.github.token"
    private const val PASTEBIN_DEV_KEY = "code-share.pastebin.dev-key"
    private const val PASTEBIN_USER_KEY = "code-share.pastebin.user-key"

    /**
     * Returns the configured GitHub token.
     *
     * @return Token used for GitHub Gist uploads, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun githubToken(): String? = CredentialStoreSecrets.secret(GITHUB_TOKEN_KEY)

    /**
     * Returns the configured GitHub token asynchronously.
     *
     * @param callback Invoked with the configured token, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun githubTokenAsync(callback: (String?) -> Unit) = CredentialStoreSecrets.secretAsync(GITHUB_TOKEN_KEY, callback)

    /**
     * Stores or clears the GitHub token asynchronously.
     *
     * @param value Token value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setGithubTokenAsync(value: String?) = CredentialStoreSecrets.setSecretAsync(GITHUB_TOKEN_KEY, value)

    /**
     * Returns the configured Pastebin developer key.
     *
     * @return Developer key used by the Pastebin API, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun pastebinDeveloperKey(): String? = CredentialStoreSecrets.secret(PASTEBIN_DEV_KEY)

    /**
     * Returns the configured Pastebin developer key asynchronously.
     *
     * @param callback Invoked with the configured developer key, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun pastebinDeveloperKeyAsync(callback: (String?) -> Unit) = CredentialStoreSecrets.secretAsync(PASTEBIN_DEV_KEY, callback)

    /**
     * Stores or clears the Pastebin developer key asynchronously.
     *
     * @param value Developer key value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setPastebinDeveloperKeyAsync(value: String?) = CredentialStoreSecrets.setSecretAsync(PASTEBIN_DEV_KEY, value)

    /**
     * Returns the configured Pastebin user key.
     *
     * @return User key used for account uploads, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun pastebinUserKey(): String? = CredentialStoreSecrets.secret(PASTEBIN_USER_KEY)

    /**
     * Returns the configured Pastebin user key asynchronously.
     *
     * @param callback Invoked with the configured user key, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun pastebinUserKeyAsync(callback: (String?) -> Unit) = CredentialStoreSecrets.secretAsync(PASTEBIN_USER_KEY, callback)

    /**
     * Stores or clears the Pastebin user key asynchronously.
     *
     * @param value User key value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setPastebinUserKeyAsync(value: String?) = CredentialStoreSecrets.setSecretAsync(PASTEBIN_USER_KEY, value)
}
