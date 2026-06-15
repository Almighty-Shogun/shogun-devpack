package ms.shogun.devpack.settings

import com.intellij.openapi.project.Project
import com.intellij.openapi.options.SearchableConfigurable

/**
 * Base class for settings pages that are scoped to a single IntelliJ project.
 *
 * @param project Project owning the configurable.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
abstract class BaseProjectConfigurable(protected val project: Project) : SearchableConfigurable
