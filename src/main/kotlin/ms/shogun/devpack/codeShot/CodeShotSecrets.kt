package ms.shogun.devpack.codeShot

import ms.shogun.devpack.utils.CredentialStoreSecrets

/**
 * Stores sensitive Code Shot credentials in the IDE credential store.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodeShotSecrets {
    private const val IMGBB_API_KEY = "code-shot.imgbb.api-key"
    private const val FREEIMAGE_HOST_API_KEY = "code-shot.freeimage-host.api-key"

    /**
     * Returns the configured Freeimage.host API key.
     *
     * @return Freeimage.host API key, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun freeimageHostApiKey(): String? = CredentialStoreSecrets.secret(FREEIMAGE_HOST_API_KEY)

    /**
     * Returns the configured Freeimage.host API key asynchronously.
     *
     * @param callback Invoked with the configured API key, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun freeimageHostApiKeyAsync(callback: (String?) -> Unit) =
        CredentialStoreSecrets.secretAsync(FREEIMAGE_HOST_API_KEY, callback)

    /**
     * Stores or clears the Freeimage.host API key asynchronously.
     *
     * @param value API key value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setFreeimageHostApiKeyAsync(value: String?) =
        CredentialStoreSecrets.setSecretAsync(FREEIMAGE_HOST_API_KEY, value)

    /**
     * Returns the configured ImgBB API key.
     *
     * @return ImgBB API key, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun imgbbApiKey(): String? = CredentialStoreSecrets.secret(IMGBB_API_KEY)

    /**
     * Returns the configured ImgBB API key asynchronously.
     *
     * @param callback Invoked with the configured API key, or `null` when unset.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun imgbbApiKeyAsync(callback: (String?) -> Unit) = CredentialStoreSecrets.secretAsync(IMGBB_API_KEY, callback)

    /**
     * Stores or clears the ImgBB API key asynchronously.
     *
     * @param value API key value, or `null` to clear it.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun setImgbbApiKeyAsync(value: String?) = CredentialStoreSecrets.setSecretAsync(IMGBB_API_KEY, value)
}
