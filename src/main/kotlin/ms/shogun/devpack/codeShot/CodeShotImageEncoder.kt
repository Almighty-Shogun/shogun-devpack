package ms.shogun.devpack.codeShot

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import javax.imageio.ImageIO

/**
 * Encodes rendered Code Shot images for upload.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object CodeShotImageEncoder {
    /**
     * Encodes a rendered screenshot as PNG bytes.
     *
     * @param image Rendered screenshot.
     *
     * @return PNG bytes.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun pngBytes(image: BufferedImage): ByteArray = ByteArrayOutputStream().use { outputStream ->
        ImageIO.write(image, "png", outputStream)
        outputStream.toByteArray()
    }
}
