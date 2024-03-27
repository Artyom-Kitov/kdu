package me.akitov.kdu.model

import me.akitov.kdu.KduVisitor
import java.io.PrintStream

class KduPrinter(stream: PrintStream, maxDepth: Int, limit: Int) {
    private val printer = PrinterVisitor(stream, maxDepth, limit)

    fun print(file: KduFile?) {
        if (file == null) {
            return
        }
        file.accept(printer)
    }

    private class PrinterVisitor(
        private val stream: PrintStream,
        private val maxDepth: Int,
        private val limit: Int
    ) : KduVisitor {
        private var currentDepth = 0
        private val comparator = Comparator<KduFile> {
            a, b -> when {
                a == null && b == null -> 0
                a == null -> 1
                else -> -1
            }
        }

        override fun visit(regularFile: KduRegularFile) {
            stream.println(" ".repeat(currentDepth) + regularFile.path.fileName + sizeSuffix(regularFile))
        }

        override fun visit(symlink: KduSymlink) {
            stream.println(" ".repeat(currentDepth) + symlink.path.fileName + " [symlink]")
            if (currentDepth < maxDepth && symlink.target != null) {
                currentDepth++
                symlink.target.accept(this)
                currentDepth--
            }
        }

        override fun visit(directory: KduDirectory) {
            stream.println("  ".repeat(currentDepth) + "/" + directory.path.fileName + sizeSuffix(directory))
            if (currentDepth == maxDepth || !directory.isAccessible) {
                return
            }
            val sorted = directory.children.sortedWith(comparator)
            currentDepth++
            var i = 0
            while (i < sorted.size && i < limit) {
                sorted[i].accept(this)
                i++
            }
            currentDepth--
        }

        companion object {
            private fun sizeSuffix(file: KduFile): String {
                if (!file.isAccessible) {
                    return " [unknown"
                }
                var size = file.byteSize.toFloat()
                var suffix = " B"
                if (size / 1024 >= 1) {
                    size /= 1024
                    suffix = " KiB"
                }
                if (size / 1024 >= 1) {
                    size /= 1024
                    suffix = " MiB"
                }
                return " [${String.format("%.3f", size)}${suffix}]"
            }
        }
    }
}