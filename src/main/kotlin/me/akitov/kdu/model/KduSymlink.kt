package me.akitov.kdu.model

import me.akitov.kdu.KduVisitor
import java.nio.file.Path

class KduSymlink(override val path: Path, val target: KduFile?) : KduFile(path, 0) {
    override val isAccessible
        get() = target != null

    override fun accept(visitor: KduVisitor) = visitor.visit(this)
}