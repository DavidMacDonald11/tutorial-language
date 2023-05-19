package sea.lexer

import java.io.File
import java.io.BufferedReader

data class SourceFile(val path: String) {
    private val file = File(path).bufferedReader()
    private var lineNum = 1

    lateinit var line: SourceLine
    val atEnd get() = (line.text == "")
    val next get() = line.nextChar

    init { setLine() }

    fun nextString(n: Int) = line.nextString(n)

    fun take(num: Int = -1, these: String = "", until: String = ""): String {
        if(atEnd || num == 0) return ""
        var string = ""

        while(!line.atEnd && string.length != num) {
            val char = line.nextChar
            if(!(these == "" || char in these) || char in until) break

            string += char
            line.stepPosition()
        }

        if(line.atEnd) readLine()
        return string
    }

    private fun readLine() {
        if(!atEnd) setLine()
    }

    private fun setLine() {
        line = SourceLine(lineNum, file.readLine()?.plus("\n")?: "")
        lineNum += 1
    }
}
