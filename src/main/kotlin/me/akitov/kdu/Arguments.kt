package me.akitov.kdu

import java.nio.file.Files
import java.nio.file.Path
import kotlin.NumberFormatException

data class Arguments(
    val depth: Int,
    val symlinksShown: Boolean,
    val limit: Int,
    val fileName: Path,
    ) {
    class Builder {
        private var depth = 8
        private var symlinksShown = false
        private var limit = 1024
        private var fileName = Path.of(".")

        companion object {
            fun buildFromStrings(vararg args: String): Arguments {
                val builder = Builder()
                var i = 0
                while (i < args.count()) {
                    when (args[i]) {
                        "--depth" -> try {
                            builder.depth(args[i + 1].toUInt().toInt())
                            i++
                        } catch (e: IndexOutOfBoundsException) {
                            throw KduException("wrong depth parameter: a positive integer value expected")
                        } catch (e: NumberFormatException) {
                            throw KduException("wrong depth parameter: a positive integer value expected")
                        }

                        "--limit" -> try {
                            builder.limit(args[i + 1].toUInt().toInt())
                            i++
                        } catch (e: IndexOutOfBoundsException) {
                            throw KduException("wrong limit parameter: a positive integer value expected")
                        } catch (e: NumberFormatException) {
                            throw KduException("wrong limit parameter: a positive integer value expected")
                        }

                        "-L" -> builder.symlinksShown(true)

                        else -> {
                            if (i == args.lastIndex) {
                                val path = Path.of(args[i])
                                if (!Files.exists(path)) {
                                    throw KduException("no such file or directory: $path")
                                }
                                builder.fileName(path)
                            } else {
                                throw KduException("no such parameter '${args[i]}'")
                            }
                        }
                    }
                    i++
                }
                return builder.build()
            }
        }

        fun depth(value: Int): Builder {
            depth = value
            return this
        }

        fun symlinksShown(value: Boolean): Builder {
            symlinksShown = value
            return this
        }

        fun limit(value: Int): Builder {
            limit = value
            return this
        }

        fun fileName(value: Path): Builder {
            fileName = value
            return this
        }

        fun build() = Arguments(depth, symlinksShown, limit, fileName)
    }
}
