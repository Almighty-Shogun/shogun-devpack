package ms.shogun.devpack.lineSorter

/**
 * Sorts selected text lines by length while keeping annotation lines attached to their owner line.
 *
 * @author Almighty-Shogun
 * @since 1.1.0
 */
object SortLinesByLengthFormatter {
    private val annotationLine = Regex("^\\s*@.+")

    /**
     * Sorts selected text by line length.
     *
     * @param text Selected editor text.
     *
     * @return Sorted text, or reversed sorted text when the input is already sorted.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    fun format(text: String): String {
        val newline = detectNewline(text)

        val groups = groups(text).ifEmpty {
            return text
        }

        val sorted = groups.sortedBy { group ->
            group.length
        }

        val normalizedInput = render(groups, "\n")
        val normalizedSorted = render(sorted, "\n")

        val result = if (normalizedInput == normalizedSorted) sorted.asReversed() else sorted

        return render(result, newline)
    }

    /**
     * Detects the selected text line separator.
     *
     * @param text Selected editor text.
     *
     * @return Selected text line separator.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun detectNewline(text: String): String =
        if (text.contains("\r\n")) {
            "\r\n"
        } else {
            "\n"
        }

    /**
     * Groups annotations with the line that follows them.
     *
     * @param text Selected editor text.
     *
     * @return Sortable line groups.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun groups(text: String): List<SortableLineGroup> {
        val groups = mutableListOf<SortableLineGroup>()
        val annotations = mutableListOf<String>()

        val lines = text.replace("\r\n", "\n").replace('\r', '\n').lines()

        lines.forEach { line ->
            if (line.isBlank()) {
                return@forEach
            }

            if (annotationLine.matches(line)) {
                annotations += line
            } else {
                groups += SortableLineGroup(annotations.toList(), line)
                annotations.clear()
            }
        }

        if (annotations.isNotEmpty()) {
            groups += SortableLineGroup(annotations.toList(), "")
        }

        return groups
    }

    /**
     * Renders line groups back into editor text.
     *
     * @param groups Sortable line groups.
     * @param newline Line separator used for rendering.
     *
     * @return Rendered editor text.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private fun render(groups: List<SortableLineGroup>, newline: String): String =
        groups
            .flatMap { group ->
                group.lines
            }
            .joinToString(newline)

    /**
     * Sortable selected-code line group.
     *
     * @property annotations Annotation lines attached to the owner line.
     * @property line Owner line used for length sorting.
     *
     * @author Almighty-Shogun
     * @since 1.1.0
     */
    private data class SortableLineGroup(val annotations: List<String>, val line: String) {
        val lines: List<String>
            get() = annotations + line

        val length: Int
            get() = line.length
    }
}
