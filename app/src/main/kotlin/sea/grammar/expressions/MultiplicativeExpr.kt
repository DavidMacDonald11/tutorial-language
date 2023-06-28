package sea.grammar

import sea.stages.*

class MultiplicativeExpr(left: Node, op: Token, right: Node)
    : BinOpNode(left, op, right)
{
    companion object: BinOpNode.CompanionObject {
        override val cls = MultiplicativeExpr::class
        override val hasList = listOf("*", "/", "%")
        override val makeChild = ExponentialExpr.Companion::construct
    }

    override fun verify(verifier: Verifier) {
        exprType = verifier.arithemticOperateOn(left.exprType, right.exprType)
    }
}
