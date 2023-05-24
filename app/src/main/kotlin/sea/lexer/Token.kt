package sea.grammar

import sea.Faults
import sea.lexer.SourceLine

private val UPPERCASE_LETTERS = ('A'..'Z').joinToString("")
private val LOWERCASE_LETTERS = ('a'..'z').joinToString("")

data class Token(val line: SourceLine, val type: Type): Faults.Component {
    enum class Type(val label: Char) {
        PUNC('P'), ID('I'), KEY('K'), NUM('N'), CHAR('C'), STR('S'), NONE('?')
    }

    val linePosition = line.newPosition()
    var string = line.newString(linePosition)

    fun of(vararg types: Type) = type in types
    fun of(c: Collection<Type>) = of(*c.toTypedArray())
    fun has(vararg strings: String) = string in strings
    fun has(c: Collection<String>) = has(*c.toTypedArray())
    fun isInt() = of(Type.NUM) && "." !in string

    override fun toString() =
        if(string == "") "${type.label}'EOF'"
        else "${type.label}'${string.replace("\n", "\\n")}'"

    override val lines = listOf(line)
    override fun treeString(prefix: String) = toString()
    override fun mark() = line.markPosition(linePosition)

    companion object {
        val PREFIX_OPS = setOf("+", "-", "!")
        val PUNCS = PREFIX_OPS + setOf(
            "*", "/", "%", "+", "-", "<<", ">>", "&", "$", "|",
            ";")
        val PUNC_SYMS = PUNCS.flatMap{it.asIterable()}.toSet()

        val ID_START_SYMS = "_" + UPPERCASE_LETTERS + LOWERCASE_LETTERS
        val ID_SYMS = ID_START_SYMS + "0123456789"

        val PRIMARY_KEYS  = setOf("true", "false")
        val KEYS = PRIMARY_KEYS + setOf(
            "as")

        val EOF = ""
        val LINE_ENDS = setOf(";", "\n", EOF)

        val NUM_START_SYMS = "0123456789."
        val NUM_SYMS = NUM_START_SYMS + "box_" + UPPERCASE_LETTERS
    }
}
