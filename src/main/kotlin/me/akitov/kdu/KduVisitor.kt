package me.akitov.kdu

import me.akitov.kdu.model.KduDirectory
import me.akitov.kdu.model.KduRegularFile
import me.akitov.kdu.model.KduSymlink

interface KduVisitor {
    fun visit(regularFile: KduRegularFile)
    fun visit(symlink: KduSymlink)
    fun visit(directory: KduDirectory)
}