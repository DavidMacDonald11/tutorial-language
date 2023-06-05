package sea.grammar

import sea.Faults
import sea.stages.*

abstract class PrimaryExpr: Node() {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser) = parser.next.let {
            when {
                it.of(Token.Type.NUM) -> NumLiteral.construct(parser)
                else -> {
                    val token = parser.take()
                    throw parser.faults.fail(token,
                        "Unexpected token in PrimaryExpression: $token")
                }
            }
        }
    }
}


class NumLiteral(token: Token, val imag: Token?): PrimaryNode(token) {
    override val parts: Parts = listOf(token, imag)

    override fun treeString(prefix: String) =
        "${this::class.simpleName} ── $token${imag?.let{" $it"}?: ""}"

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            val num = parser.expectingOf(Token.Type.NUM)
            val imag = if(parser.next.has("i")) parser.take() else null
            return NumLiteral(num, imag)
        }
    }
}
