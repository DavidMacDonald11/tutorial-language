package sea.stages

import kotlin.math.min
import sea.Faults
import sea.grammar.Token
import sea.grammar.FileStat

data class Parser(val faults: Faults, private val tokens: List<Token>) {
    init { faults.stageVerb = "Parsing" }

    private var i = 0
    val tree = FileStat()

    override fun toString() = "$tree"
    fun makeTree() = tree.makeTree(this)

    fun ahead(n: Int) = tokens[min(i + n, tokens.size - 1)]
    val next get() = ahead(0)

    fun take(): Token {
        val token = next
        i += 1
        return token
    }

    fun expectingOf(vararg types: Token.Type): Token {
        if(next.of(*types)) return take()

        val names = (types.map{it.name}).joinToString(", ", "[", "]")
        throw faults.fail(take(), "Expecting type of $names")
    }

    fun expectingHas(vararg values: String): Token {
        if(next.has(*values)) return take()

        val vals = values.joinToString(",", "[", "]")
        throw faults.fail(take(),
            "Expecting value of $vals".replace("\n", "\\n"))
    }

    fun expectingOf(c: Collection<Token.Type>) = expectingOf(*c.toTypedArray())
    fun expectingHas(c: Collection<String>) = expectingHas(*c.toTypedArray())
    fun skipNewline() { if(next.has("\n")) take() }
}
