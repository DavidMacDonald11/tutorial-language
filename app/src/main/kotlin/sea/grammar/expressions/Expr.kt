package sea.grammar

import sea.stages.Parser

abstract class Expr: Node() {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            return AdditiveExpr.construct(parser)
        }
    }
}
