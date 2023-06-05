package sea.grammar

import sea.stages.*

data class EndedStat(val stat: Node): Node() {
    override val parts: Parts = listOf(stat)

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            val node = Stat.construct(parser)
            parser.expectingHas(Token.LINE_ENDS)
            return EndedStat(node)
        }
    }
}
