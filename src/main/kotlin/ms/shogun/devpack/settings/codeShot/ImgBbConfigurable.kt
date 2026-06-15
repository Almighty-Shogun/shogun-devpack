package ms.shogun.devpack.settings.codeShot

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShot.CodeShotSecrets

/**
 * Application settings page for ImgBB Code Shot uploads.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class ImgBbConfigurable : BaseHostedImageConfigurable(
    id = "ms.shogun.devpack.settings.code-shot.imgbb",
    texts = HostedImageSettingsTexts(
        displayName = message("settings.imgbb.display-name"),
        notice = message("settings.code-shot.imgbb.notice"),
        apiKeyLabel = message("settings.code-shot.imgbb.api-key"),
        apiKeyDescription = message("settings.code-shot.imgbb.api-key.description")
    ),
    apiKeyReader = CodeShotSecrets::imgbbApiKeyAsync,
    apiKeyWriter = CodeShotSecrets::setImgbbApiKeyAsync
)
