package me.akitov.kdu.model

import me.akitov.kdu.KduVisitor
import java.nio.file.Path

class KduDirectory(path: Path, byteSize: Long, var children: List<KduFile>) : KduFile(path, byteSize) {
    override val isAccessible
        get() = children.isNotEmpty()

    override fun accept(visitor: KduVisitor) = visitor.visit(this)
}