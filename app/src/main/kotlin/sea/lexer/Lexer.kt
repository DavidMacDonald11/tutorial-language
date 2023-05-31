package sea.lexer

import kotlin.math.pow
import sea.Faults
import sea.grammar.Token

data class Lexer(val faults: Faults, val filePath: String) {
    val file = SourceFile(filePath)
    val tokens = mutableListOf<Token>()
    var atStringStart = false

    override fun toString() = tokens.joinToString(", ", "[", "]")

    fun makeTokens() {
        do makeToken() while(!file.atEnd)
    }

    private fun newTempToken(
        type: Token.Type = Token.Type.NONE,
        line: SourceLine = file.line,
        isStringEnd: Boolean = false
    ): Token {
        val token = Token(type, line)

        token.isStringEnd = isStringEnd
        token.isStringStart = atStringStart
        if(atStringStart) atStringStart = false

        return token
    }

    private fun newToken(
        type: Token.Type = Token.Type.NONE,
        line: SourceLine = file.line,
        isStringEnd: Boolean = false
    ): Token {
        val token = newTempToken(type, line, isStringEnd)
        tokens.add(token)
        return token
    }

    private fun makeToken() {
        ignoreSpacesAndComments()

        when(file.next) {
            '\u0000' -> {
                file.take(1)
                newToken(Token.Type.PUNC)
            }
            in Token.NUM_START_SYMS -> makeNumber()
            in Token.PUNC_SYMS -> makePunctuator()
            in Token.ID_START_SYMS -> makeIdentifier()
            '\'' -> makeCharacter()
            '"' -> makeString()
            else -> {
                file.take(1)
                val token = newToken()

                throw faults.fail(token,
                    "Unrecognized symbol '${token.string}'")
            }
        }
    }

    private fun makeNumber() {
        val d = "[0-9A-Z]"
        val pattern = """
                   (\d+) | (\d*\.\d+) | (\d+\.\d*)  |
             (\d+b(($d+) | ($d*\.$d+) | ($d+\.$d*)))|
            (0[xo](($d+) | ($d*\.$d+) | ($d+\.$d*)))
        """.replace("\\s".toRegex(), "").toRegex()

        if(file.nextString(2).matches("""\.[^\d_]?""".toRegex())) {
            return makePunctuator()
        }

        file.take(these = Token.NUM_SYMS)

        val token = newToken(Token.Type.NUM)
        token.string = token.string.replace("_", "")

        if(token.string.matches(pattern)) convertNumber(token)
        else faults.error(token, "Invalid numeric literal")
    }

    private fun convertNumber(token: Token) {
        var string = token.string
        if(listOf('b', 'o', 'x').all{it !in string}) return

        var base = if('o' in string) 8.0
            else if('x' in string) 16.0
            else string.substring(0, string.indexOf("b")).toDouble()

        if(base < 1) base = 2.0
        if(base < 2) throw faults.fail(token, "Numeric base cannot be 1")

        string = string.replace("o|x".toRegex(), "b")
        val num = string.substring(string.indexOf("b") + 1)

        var result = 0.0
        var (int, frac) =
            num.split(".").let{Pair(it[0], it.getOrElse(1){""})}

        val errorTemplate = "is an invalid base ${base.toInt()} digit"

        for((i, c) in int.withIndex()) {
            val digit = if(c.isDigit()) c - '0' else c - 'A' + 10
            if(digit >= base) throw faults.fail(token, "$c $errorTemplate")
            result += digit * base.pow(int.length - i - 1)
        }

        for((i, c) in frac.withIndex()) {
            val digit = if(c.isDigit()) c - '0' else c - 'A' + 10
            if(digit >= base) throw faults.fail(token, "$c $errorTemplate")
            result += digit * base.pow(-(i + 1))
        }

        token.string = if(token.isInt) "${result.toLong()}" else "$result"
    }

    private fun makePunctuator() {
        if(file.nextString(3) in Token.PUNCS) file.take(3)
        else if(file.nextString(2) in Token.PUNCS) file.take(2)
        else file.take(1)

        val token = newToken(Token.Type.PUNC)
        if(token.string !in Token.PUNCS) faults.error(token,
            "Unrecognized punctuator")
    }

    private fun makeIdentifier() {
        val string = file.take(these = Token.ID_SYMS)
        val type = if(string in Token.KEYS) Token.Type.KEY
            else Token.Type.ID

        newToken(type)
    }

    private fun makeCharacter() {
        val pattern = """'(\\[btnr'"\\$]|\\u[a-fA-F0-9]{4}|[^\\'])'""".toRegex()
        file.take(1)

        if(file.next == '\'') {
            file.take(1)
            faults.error(newToken(Token.Type.CHAR), "Empty character literal")
            return
        }

        if(file.next == '\\') {
            file.take(2)
            file.take(until = "'")
        } else file.take(1)

        if(file.take(1) != "'") throw faults.fail(newToken(),
            "Unterminated character literal")

        val token = newToken(Token.Type.CHAR)

        if(!token.string.matches(pattern)) faults.error(token,
            "Invalid character literal")
    }

    private fun makeString() {
        atStringStart = true

        if(file.nextString(3) == "\"\"\"") return makeMultilineString()
        file.take(1)

        while(file.next != '\n') {
            file.take(until = "\\$\"\n")

            when(file.next) {
                '\\' -> makeStringEscapeSequence()
                '$' -> makeStringTemplate()
                '\"' -> {
                    file.take(1)
                    newToken(Token.Type.STR, isStringEnd = true)
                    return
                }
                '\n' -> break
            }
        }

        throw faults.fail(newToken(), "Unterminated string literal")
    }

    private fun makeMultilineString() {
        file.take(3)

        while(!file.atEnd) {
            if(file.nextString(3) == "\"\"\"") {
                file.take(3)
                newToken(Token.Type.STR, isStringEnd = true)
                return
            }

            file.take(until = "$\n")

            if(file.next == '\n') {
                val line = file.line
                file.take(1)
                newToken(Token.Type.STR, line)
            } else if(file.next == '$') makeStringTemplate()
        }

        throw faults.fail(newToken(), "Unterminated multiline string literal")
    }

    private fun makeStringEscapeSequence() {
        val pattern = """\\[btnr'"\\$]|\\u[a-fA-F0-9]{4}""".toRegex()

        var escape = file.take(2)
        if(escape == "\\u") escape += file.take(4)

        if(!escape.matches(pattern)) faults.error(newToken(),
            "Invalid escape sequence")
    }

    private fun makeStringTemplate() {
        when(file.nextString(2)[1]) {
            in Token.ID_START_SYMS -> makeSmallStringTemplate()
            '{' -> makeLargeStringTemplate()
            else -> file.take(1)
        }
    }

    private fun makeSmallStringTemplate() {
        newToken(Token.Type.STR)

        file.take(1)
        file.line.ignorePosition()

        makeIdentifier()
        return
    }

    private fun makeLargeStringTemplate() {
        newToken(Token.Type.STR)

        file.take(2)
        file.line.ignorePosition()

        if(file.next == '}') {
            file.take(1)
            faults.warn(newTempToken(), "Empty string template")
            return
        }

        while(true) {
            ignoreSpacesAndComments()
            if(file.next in listOf('}', '\u0000')) break
            makeToken()
        }

        val nextIsNothing = (file.next == '\u0000')
        file.take(1)
        file.line.ignorePosition()

        if(nextIsNothing) throw faults.fail(newToken(),
            "Unterminated string template")
    }

    private fun ignoreSpacesAndComments() {
        while(ignoreSpace() || ignoreComment()) {}
    }

    private fun ignoreSpace(): Boolean {
        if(!file.next.isWhitespace()) return false

        if(file.next != '\n') {
            file.take(1)
            file.line.ignorePosition()
            return true
        }

        val line = file.line
        file.take(1)

        val lastWasNewline = tokens.size == 0 || tokens.last().has("\n")

        if(!lastWasNewline) newToken(Token.Type.PUNC, line)
        else file.line.ignorePosition()

        return true
    }

    private fun ignoreComment(): Boolean {
        when(file.nextString(2)) {
            "//" -> {
                file.take(until = "\n")
                file.line.ignorePosition()
            }
            "/*" -> ignoreMultilineComment()
            else -> return false
        }

        return true
    }

    private fun ignoreMultilineComment() {
        val line = file.line
        file.take(2)

        while(!file.atEnd) {
            if(file.nextString(2) != "*/") {
                file.take(1)
                file.take(until = "*")
                continue
            }

            file.take(2)
            file.line.ignorePosition()
            return
        }

        throw faults.fail(newToken(line = line),
            "Unterminated multiline comment")
    }
}
