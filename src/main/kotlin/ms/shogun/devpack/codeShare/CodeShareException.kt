package ms.shogun.devpack.codeShare

/**
 * Failure raised while preparing or uploading shared code.
 *
 * @param message Human-readable failure reason.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class CodeShareException(message: String) : RuntimeException(message)
