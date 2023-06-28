package sea.stages

import sea.Faults
import sea.grammar.Node
import sea.grammar.ExprType

data class Verifier(val faults: Faults, private val tree: Node) {
    init { faults.stageVerb = "Verifying" }

    val nodes = ArrayDeque<Node>()
    val node get() = nodes.last()

    override fun toString() = "$tree"
    fun verifyTree() = tree.verifyNode(this)

    fun operateOn(t1: ExprType, t2: ExprType? = null): ExprType {
        val types = listOfNotNull(t1, t2)

        for(type in types) {
            if(!type.canOperateOn) {
                faults.error(node, "Cannot operate on type $type")
                return types.last()
            }
        }

        val type = ExprType.operateOn(t1, t2)
        if(type == null) faults.error(node, "Cannot operate on $t1 and $t2")
        return type?:types.last()
    }

    fun arithemticOperateOn(t1: ExprType, t2: ExprType? = null): ExprType {
        val type = operateOn(t1, t2)

        if(!type.isNumber) faults.error(node,
            "Cannot perform arithemtic operation on non-number type $type")

        return type
    }

    fun bitwiseOperateOn(t1: ExprType, t2: ExprType? = null): ExprType {
        val type = operateOn(t1, t2)

        if(!type.isInteger) faults.error(node,
            "Cannot perform bitwise operateion on non-integer type $type")

        return type
    }
}
