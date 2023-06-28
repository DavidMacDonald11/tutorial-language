package sea.grammar

import sea.stages.*

data class ExponentialExpr(val left: Node, val op: Token, val right: Node)
    : Node()
{
    override val parts: Parts = listOf(left, op, right)

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            var node = PrimaryExpr.construct(parser)
            if(!parser.next.has("^")) return node

            val op = parser.take()
            parser.skipNewline()

            val right = ExponentialExpr.construct(parser)
            return ExponentialExpr(node, op, right)
        }
    }

    override fun verify(verifier: Verifier) {
        exprType = verifier.arithemticOperateOn(left.exprType, right.exprType)
    }
}
