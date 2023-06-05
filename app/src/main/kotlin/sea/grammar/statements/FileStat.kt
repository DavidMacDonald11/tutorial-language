package sea.grammar

import sea.stages.*

class FileStat: Node() {
    private val statements = mutableListOf<Node>()
    override val parts: Parts = statements

    fun makeTree(parser: Parser) {
        while(!parser.next.has(Token.EOF)) {
            parser.skipNewline()
            if(parser.next.has(Token.EOF)) break

            statements += EndedStat.construct(parser)
        }
    }
}
