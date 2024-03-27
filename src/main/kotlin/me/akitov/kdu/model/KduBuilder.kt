package me.akitov.kdu.model

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class KduBuilder(private val buildSymlinks: Boolean) {
    private val built = mutableMapOf<Path, KduFile?>()

    companion object {
        fun build(path: Path, buildSymlinks: Boolean): KduFile? {
            val builder = KduBuilder(buildSymlinks)
            return builder.buildImpl(path)
        }
    }

    private fun buildImpl(path: Path): KduFile? {
        val result = when {
            Files.isSymbolicLink(path) -> buildSymlink(path)
            Files.isDirectory(path) -> buildDirectory(path)
            Files.isRegularFile(path) -> buildRegularFile(path)
            else -> {
                println("Something unknown happened while building '${path.toAbsolutePath()}', skipping")
                null
            }
        }
        if (result != null) {
            built[path] = result
        }
        return result
    }

    private fun buildRegularFile(path: Path): KduFile {
        val byteSize = try {
            Files.size(path)
        } catch (e: IOException) {
            println("Couldn't get the actual size of file: ${e.message}")
            -1
        }
        return KduRegularFile(path, byteSize)
    }

    private fun buildSymlink(path: Path): KduFile {
        if (!buildSymlinks) {
            return KduSymlink(path, null)
        }
        var target: KduFile? = null
        try {
            val p = Files.readSymbolicLink(path)
            if (!Files.exists(p)) {
                return KduSymlink(path, null)
            }
            target = built[p]
            if (target == null) {
                target = buildImpl(p)
                built[p] = target
            }
        } catch (e: IOException) {
            println("Couldn't read symlink: ${e.message}")
        }
        return KduSymlink(path, target)
    }

    private fun buildDirectory(path: Path): KduFile {
        val result = KduDirectory(path, 0, listOf())
        built[path] = result

        val children: MutableList<KduFile> = mutableListOf()
        var byteSize = 0L
        try {
            val contentStream = Files.list(path)
            contentStream.use {
                val contentArray: Array<Path> = contentStream.toArray {
                    n -> arrayOfNulls(n)
                }
                for (p in contentArray) {
                    var file = built[p]
                    if (file == null) {
                        file = buildImpl(p)
                    }
                    if (file != null) {
                        children.add(file)
                        byteSize += file.byteSize
                    }
                }
            }
        } catch (e: IOException) {
            println("Couldn't read directory: ${e.message}")
            children.clear()
        }
        result.byteSize = byteSize
        result.children = children
        return result
    }
}