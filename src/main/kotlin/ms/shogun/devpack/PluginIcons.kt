package ms.shogun.devpack

import javax.swing.Icon

import com.intellij.openapi.util.IconLoader

/**
 * Central registry for SVG icons loaded from plugin resources.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object PluginIcons {
    val branding = loadIcon("/icons/logo/app-icon-32.svg")
    val brandingSmall = loadIcon("/icons/logo/app-icon-16.svg")

    val vue = loadIcon("/icons/files/vue.svg")
    val json = loadIcon("/icons/files/json.svg")
    val csharp = loadIcon("/icons/files/csharp.svg")
    val markdown = loadIcon("/icons/files/markdown.svg")
    val typescript = loadIcon("/icons/files/typescript.svg")
    val license = loadIcon("/icons/files/license.svg")

    /**
     * Loads the icons of the given path.
     *
     * @param path The path were the icon is located.
     *
     * @returns The loaded icon.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun loadIcon(path: String): Icon = IconLoader.getIcon(path, javaClass)
}
