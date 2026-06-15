package ms.shogun.devpack.files.validators

import com.intellij.openapi.ui.InputValidatorEx

import ms.shogun.devpack.ShogunBundle.message

/**
 * Validates template file names and nested path segments.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
object FileNameValidator : InputValidatorEx {
    private val INVALID_FILE_NAME_CHARACTERS = setOf('<', '>', ':', '"', '\\', '|', '?', '*')

    override fun canClose(inputString: String?): Boolean = checkInput(inputString)

    override fun getErrorText(inputString: String?): String = message("file.name.invalid", inputString.orEmpty())

    override fun checkInput(inputString: String?): Boolean {
        val name = inputString.orEmpty()

        return name.isNotBlank() &&
            name.split('/').all(::isValidPathSegment)
    }

    /**
     * Validates one path segment while allowing nested directory creation via `/`.
     *
     * @param segment Single path segment between `/` separators.
     *
     * @return `true` when the segment is safe to use as a directory or file name.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun isValidPathSegment(segment: String): Boolean =
        segment.isNotBlank() &&
            segment != "." &&
            segment != ".." &&
            segment.none { it.isISOControl() || it in INVALID_FILE_NAME_CHARACTERS } &&
            !segment.endsWith('.') &&
            !segment.endsWith(' ')
}
