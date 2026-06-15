package ms.shogun.devpack.settings.codeShot

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShot.CodeShotSecrets

/**
 * Application settings page for Freeimage.host Code Shot uploads.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class FreeimageHostConfigurable : BaseHostedImageConfigurable(
    id = "ms.shogun.devpack.settings.code-shot.freeimage-host",
    texts = HostedImageSettingsTexts(
        displayName = message("settings.freeimage-host.display-name"),
        notice = message("settings.code-shot.freeimage-host.notice"),
        apiKeyLabel = message("settings.code-shot.freeimage-host.api-key"),
        apiKeyDescription = message("settings.code-shot.freeimage-host.api-key.description")
    ),
    apiKeyReader = CodeShotSecrets::freeimageHostApiKeyAsync,
    apiKeyWriter = CodeShotSecrets::setFreeimageHostApiKeyAsync
)
