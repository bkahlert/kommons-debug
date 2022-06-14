package com.bkahlert.kommons

import com.bkahlert.kommons.debug.`class`
import com.bkahlert.kommons.debug.findSourceFileOrNull
import kotlin.io.path.pathString
import kotlin.io.path.readLines

data class FileInfo(
    val lineNumber: Int,
    val sourceFileName: String,
    val line: String,
    val methodName: String,
)

object FilePeekMPP {
    fun getCallerFileInfo(stackTraceElement: StackTraceElement): FileInfo? {
        val sourceFile = stackTraceElement.`class`.findSourceFileOrNull(stackTraceElement.fileName) ?: return null
        val (lines, lineNumber) = sourceFile.readLines().let { lines ->
            if (stackTraceElement.lineNumber < lines.size) {
                // looks like not inlined
                lines.drop(stackTraceElement.lineNumber - 1) to stackTraceElement.lineNumber
            } else {
                // obviously inlined since line number > available lines
                val classNames = stackTraceElement.className.split("$").map { it.substringAfterLast('.') }
                val relevantLines = classNames.fold(lines) { remainingLines, className ->
                    remainingLines
                        .dropWhile { line -> !line.contains(className) }
                        .findBlock()
                        .takeUnless { it.isEmpty() } ?: remainingLines
                }.dropWhile { !it.contains('{') }
                val fullText = lines.joinToString(LineSeparators.Default)
                val relevantFullText = relevantLines.joinToString(LineSeparators.Default)
                relevantLines to fullText.substringBefore(relevantFullText).lines().size
            }
        }

        val callerLine: String = lines.findBlock().joinToString(separator = "") { it.trim() }

        return FileInfo(
            lineNumber,
            sourceFileName = sourceFile.pathString,
            line = callerLine.trim(),
            methodName = stackTraceElement.methodName
        )
    }

    private fun List<String>.findBlock(): List<String> {
        var braceDelta = 0
        return takeWhileInclusive { line ->
            val openBraces = line.count { it == '{' }
            val closeBraces = line.count { it == '}' }
            braceDelta += openBraces - closeBraces
            braceDelta != 0
        }
    }

    private fun <T> List<T>.takeWhileInclusive(pred: (T) -> Boolean): List<T> {
        var shouldContinue = true
        return takeWhile {
            val result = shouldContinue
            shouldContinue = pred(it)
            result
        }
    }
}

object LambdaBody {

    fun getOrNull(
        stackTraceElement: StackTraceElement,
        explicitMethodName: String? = null,
    ) = kotlin.runCatching {
        val line = FilePeekMPP.getCallerFileInfo(stackTraceElement)?.line ?: return null
        if (explicitMethodName != null) {
            line.takeIf { it.contains(explicitMethodName) }?.let {
                LambdaBody(explicitMethodName, it)
            }
        } else {
            LambdaBody(stackTraceElement.methodName, line)
        }
    }.getOrNull()

    operator fun invoke(methodName: String, line: String): String {
        val firstPossibleBracket = line.indexOf(methodName).takeIf { it >= 0 }?.let { it + methodName.length } ?: 0
        val firstBracket = line.indexOf('{', firstPossibleBracket) + 1
        val subjectEnd = findMatchingClosingBracket(line, firstBracket)
        return line.substring(firstBracket, subjectEnd).trim()
    }

    private fun findMatchingClosingBracket(condition: String, start: Int): Int {
        val len = condition.length
        var bracketLevel = 0
        var pos = start
        while (pos < len) {
            when (condition[pos]) {
                '{' -> bracketLevel += 1
                '}' -> if (bracketLevel == 0) return pos else bracketLevel -= 1
            }
            pos += 1
        }
        error("could not find matching brackets in $condition")
    }
}
