package me.akitov.kdu.model

import me.akitov.kdu.KduVisitor
import java.nio.file.Path

abstract class KduFile(open val path: Path, open var byteSize: Long) {
    abstract val isAccessible: Boolean

    abstract fun accept(visitor: KduVisitor)

    override fun equals(other: Any?) =
        other != null && other is KduFile && javaClass == other.javaClass && path == other.path

    override fun hashCode() = path.hashCode()
}