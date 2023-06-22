package sea.stages

import sea.Faults
import sea.grammar.Node
import sea.grammar.BasicType
import sea.grammar.ExprType

data class Verifier(val faults: Faults, private val tree: Node) {
    val nodes = ArrayDeque<Node>()
    val node get() = nodes.last()

    fun verifyTree() = tree.verifyNode(this)
}
