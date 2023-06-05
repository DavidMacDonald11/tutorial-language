package sea.grammar

import sea.stages.*

abstract class BasicStat: Node() {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            if(parser.next.has("print")) return PrintStat.construct(parser)
            return Expr.construct(parser)
        }
    }
}


data class PrintStat(val expr: Node): Node() {
    override val parts: Parts = listOf(expr)

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            parser.expectingHas("print")
            val expr = Expr.construct(parser)
            return PrintStat(expr)
        }
    }
}
