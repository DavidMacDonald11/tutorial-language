package sea.grammar

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import sea.Faults
import sea.files.SourceLine
import sea.stages.*

typealias Parts = List<Faults.Component?>

abstract class Node: Faults.Component {
    abstract val parts: Parts
    lateinit var exprType: ExprType

    interface CompanionObject {
        fun construct(parser: Parser): Node?
    }

    open fun verify(verifier: Verifier) {
        exprType = ExprType(ExprType.Base.Unit)
    }

    override val lines: List<SourceLine> get() {
        val lines = mutableSetOf<SourceLine>()
        for(part in parts) part?.let{ lines += it.lines.toSet() }
        return lines.toList().sortedBy{it.num}
    }

    protected val nameAndType get(): String {
        var string = "${this::class.simpleName}"
        if(::exprType.isInitialized) string += ": ${exprType}"
        return string
    }


    override fun treeString(prefix: String): String {
        val parts = parts.filterNotNull()
        var string = nameAndType

        for((i, part) in parts.withIndex()) {
            val atEnd = (i == parts.size - 1)
            val symbol = if(atEnd) "└──" else "├──"

            val childSymbol = if(atEnd) " " else "│"
            val children = part.treeString("$prefix$childSymbol   ")
            string += "\n$prefix$symbol $children"
        }

        return string
    }

    override fun mark() = parts.forEach{ it?.mark() }
    override fun toString() = treeString("    ")

    fun verifyNode(verifier: Verifier) {
        verifier.nodes.addLast(this)

        val nodes = parts.mapNotNull{it as? Node}
        nodes.forEach{ it.verifyNode(verifier) }
        verify(verifier)

        verifier.nodes.removeLast()
    }
}


abstract class PrimaryNode(var token: Token): Node() {
    override val parts: Parts = listOf(token)
    override fun treeString(prefix: String) =
        "$nameAndType ── $token"
}


abstract class BinOpNode(var left: Node, var op: Token, var right: Node): Node()
{
    override val parts: Parts = listOf(left, op, right)

    interface CompanionObject: Node.CompanionObject {
        val cls: KClass<*>
        val hasList: List<String>
        val makeChild: (Parser) -> Node

        override fun construct(parser: Parser): Node {
            var node = makeChild(parser)

            while(parser.next.has(hasList)) {
                val op = parser.take()
                parser.skipNewline()

                val right = makeChild(parser)
                node = cls.primaryConstructor!!.call(node, op, right) as Node
            }

            return node
        }
    }
}
