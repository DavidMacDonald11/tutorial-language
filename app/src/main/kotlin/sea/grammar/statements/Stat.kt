package sea.grammar

import sea.stages.*

abstract class Stat: Node() {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            return BasicStat.construct(parser)
        }
    }
}
