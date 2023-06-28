package sea.grammar

import sea.stages.*

class AdditiveExpr(left: Node, op: Token, right: Node)
    : BinOpNode(left, op, right)
{
    companion object: BinOpNode.CompanionObject {
        override val cls = AdditiveExpr::class
        override val hasList = listOf("+", "-")
        override val makeChild = MultiplicativeExpr.Companion::construct
    }

    override fun verify(verifier: Verifier) {
        exprType = verifier.arithemticOperateOn(left.exprType, right.exprType)
    }
}
