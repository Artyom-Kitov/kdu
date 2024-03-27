package me.akitov.kdu.model

import me.akitov.kdu.KduVisitor
import java.nio.file.Path

class KduRegularFile(override val path: Path, override var byteSize: Long) : KduFile(path, byteSize) {
    override val isAccessible: Boolean
        get() = byteSize != -1L

    override fun accept(visitor: KduVisitor) = visitor.visit(this)
}