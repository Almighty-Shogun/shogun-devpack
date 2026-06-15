package ms.shogun.devpack.utils

/**
 * Converts display or class names into kebab-case identifiers.
 *
 * @param value Text to normalize.
 *
 * @return Kebab-case value containing lowercase words separated by `-`.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
fun toKebabCase(value: String): String =
    value.replace(Regex("([a-z0-9])([A-Z])"), "$1-$2")
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1-$2")
        .replace(Regex("[^A-Za-z0-9]+"), "-")
        .trim('-')
        .lowercase()

/**
 * Converts display, file, or identifier names into PascalCase identifiers.
 *
 * @param value Text to normalize.
 *
 * @return PascalCase value containing only alphanumeric word parts.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
fun toPascalCase(value: String): String =
    value.replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
        .replace(Regex("[^A-Za-z0-9]+"), " ")
        .trim()
        .split(Regex("\\s+"))
        .filter { part ->
            part.isNotBlank()
        }
        .joinToString("") { part ->
            part.replaceFirstChar { character ->
                character.uppercase()
            }
        }
