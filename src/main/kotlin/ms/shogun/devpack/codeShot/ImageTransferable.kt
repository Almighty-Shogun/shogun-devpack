package ms.shogun.devpack.codeShot

import java.awt.Image
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

/**
 * Clipboard transferable for rendered screenshot images.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class ImageTransferable(private val image: Image) : Transferable {
    override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(DataFlavor.imageFlavor)

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor == DataFlavor.imageFlavor

    override fun getTransferData(flavor: DataFlavor): Any =
        if (isDataFlavorSupported(flavor)) {
            image
        } else {
            throw UnsupportedFlavorException(flavor)
        }
}
