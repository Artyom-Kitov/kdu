package me.akitov.kdu

import me.akitov.kdu.model.KduBuilder
import me.akitov.kdu.model.KduPrinter

fun main(args: Array<String>) {
    val arguments = try {
        Arguments.Builder.buildFromStrings(*args)
    } catch (e: KduException) {
        println("Error: ${e.message}")
        println(usage())
        return
    } catch (e: Exception) {
        println("Something unknown happened, please try again or contact a developer")
        e.printStackTrace()
        return
    }

    val file = KduBuilder.build(arguments.fileName, arguments.symlinksShown)
    val printer = KduPrinter(System.out, arguments.depth, arguments.limit)
    printer.print(file)
}

fun usage() = """
            Usage: ./jdu [OPTIONS] [FILE]
            Summarize disk usage of a file, recursively for directories.
                          
            Possible options:
              --depth n   Max recursion depth (8 by default).
              -L          Show symlinks.
              --limit n   Show n heaviest files/directories in every directory.""".trimIndent()