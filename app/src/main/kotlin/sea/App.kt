package sea

import SeaLexer
import SeaParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

class SeaPrintableTree(val tree: ParseTree) {
    override fun toString() = treeString(tree, "")

    private fun treeString(node: ParseTree, prefix: String): String {
        if(node is SeaParser.PrimaryExprContext && node.childCount == 1)
            return visitPrimary(node)

        if(node is TerminalNode) return visitTerminal(node)
        if(node !is RuleNode) return ""

        val name = SeaParser.ruleNames[node.ruleContext.ruleIndex]
        val builder = StringBuilder(name)

        for(i in 0..(node.childCount - 1)) {
            val atEnd = (i == node.childCount - 1)
            val symbol = if(atEnd) "└──" else "├──"

            val child = node.getChild(i)
            val childSymbol = if(atEnd) " " else "│"
            val childStr = treeString(child, "$prefix$childSymbol   ")

            builder.append("\n$prefix$symbol $childStr")
        }

        return "$builder"
    }

    private fun visitPrimary(node: SeaParser.PrimaryExprContext): String {
        val name = SeaParser.ruleNames[node.ruleContext.ruleIndex]
        val childStr = visitTerminal(node.getChild(0) as TerminalNode)
        return "$name ── $childStr"
    }

    private fun visitTerminal(node: TerminalNode): String {
        val id = SeaLexer
            .ruleNames[node.symbol.type - 1]
            .let { if("T__" in it) 'P' else it[0] }

        return "$id'$node'"
    }

}

fun main() {
    val input = "5838 + 44 - 3"
    val inputStream = CharStreams.fromString(input)

    val lexer = SeaLexer(inputStream)
    val tokens = CommonTokenStream(lexer)

    val parser = SeaParser(tokens)
    val tree = parser.fileStat()

    val printableTree = SeaPrintableTree(tree)
    println(printableTree)
}
