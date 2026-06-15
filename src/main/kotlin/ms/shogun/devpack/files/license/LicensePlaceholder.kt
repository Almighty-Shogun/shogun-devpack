package ms.shogun.devpack.files.license

import java.time.Year

/**
 * Supported user-editable license placeholders.
 *
 * @property token Placeholder token without square brackets.
 * @property label Visible field label.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
enum class LicensePlaceholder(val token: String, val label: String) {
    /**
     * Lowercase year placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    YEAR("year", "Year"),

    /**
     * Apache-style year placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    YYYY("yyyy", "Year"),

    /**
     * Capitalized year placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    YEAR_CAPITALIZED("Year", "Year"),

    /**
     * Full copyright holder placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    FULL_NAME("fullname", "Copyright holder"),

    /**
     * Long copyright owner placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    COPYRIGHT_OWNER("name of copyright owner", "Copyright holder"),

    /**
     * Long copyright holder placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    COPYRIGHT_HOLDER("name of copyright holder", "Copyright holder"),

    /**
     * Project name placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    PROJECT("project", "Project name"),

    /**
     * Project URL placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    PROJECT_URL("projecturl", "Project URL"),

    /**
     * Email placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    EMAIL("email", "Email"),

    /**
     * Software name placeholder.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    SOFTWARE_NAME("Software Name", "Software name");

    /**
     * Resolves a sensible default value for this placeholder.
     *
     * @return Default field value.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun defaultValue(): String =
        when (this) {
            YEAR,
            YYYY,
            YEAR_CAPITALIZED -> Year.now().value.toString()

            FULL_NAME,
            COPYRIGHT_OWNER,
            COPYRIGHT_HOLDER -> System.getProperty("user.name").orEmpty()

            else -> ""
        }

    /**
     * Returns this placeholder as it appears in bundled license text.
     *
     * @return Placeholder token wrapped in square brackets.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    fun markedToken(): String = "[$token]"
}
