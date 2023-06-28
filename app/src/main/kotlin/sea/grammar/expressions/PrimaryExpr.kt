package sea.grammar

import sea.Faults
import sea.stages.*

abstract class PrimaryExpr: Node() {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser) = parser.next.let {
            when {
                it.of(Token.Type.NUM) -> NumLiteral.construct(parser)
                it.of(Token.Type.STR) -> StrLiteral.construct(parser)
                it.of(Token.Type.CHAR) -> CharLiteral.construct(parser)
                it.of(Token.Type.ID) -> Identifier.construct(parser)
                it.has(Token.PRIMARY_KEYS) -> PrimaryKey.construct(parser)
                it.has("(") -> ParenExpr.construct(parser)
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
        "$nameAndType ── $token${imag?.let{" $it"}?: ""}"

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            val num = parser.expectingOf(Token.Type.NUM)
            val imag = if(parser.next.has("i")) parser.take() else null
            return NumLiteral(num, imag)
        }
    }

    override fun verify(verifier: Verifier) {
        val base = if(imag != null) ExprType.Base.Cplex64
            else if(token.isInt) ExprType.Base.Int32
            else ExprType.Base.Real64

        exprType = ExprType(base)
    }
}


class CharLiteral(token: Token): PrimaryNode(token) {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser) =
            CharLiteral(parser.expectingOf(Token.Type.CHAR))
    }

    override fun verify(verifier: Verifier) {
        exprType = ExprType(ExprType.Base.Char)
    }
}


data class StrLiteral(val components: List<Faults.Component>): Node() {
    override val parts: Parts get() = components

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            val start = parser.expectingOf(Token.Type.STR)
            if(start.isStringEnd) return StrLiteral(listOf(start))

            val parts = mutableListOf<Faults.Component>(start)
            parser.skipNewline()

            while(parser.next.isStringStart || !parser.next.isStringEnd) {
                val nextIsString = parser.next.of(Token.Type.STR)
                val nextContinues = nextIsString && !parser.next.isStringStart

                if(nextContinues) parts += parser.take()
                else {
                    parts += Expr.construct(parser)
                    parser.skipNewline()
                }
            }

            parts += parser.take()
            return StrLiteral(parts)
        }
    }

    override fun verify(verifier: Verifier) {
        exprType = ExprType(ExprType.Base.Str)
    }
}


class Identifier(token: Token): PrimaryNode(token) {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser) =
            Identifier(parser.expectingOf(Token.Type.ID))
    }

    override fun verify(verifier: Verifier) {
        exprType = ExprType(ExprType.Base.Any)
    }
}


class PrimaryKey(token: Token): PrimaryNode(token) {
    companion object: Node.CompanionObject {
        override fun construct(parser: Parser) =
            PrimaryKey(parser.expectingHas(Token.PRIMARY_KEYS))
    }

    override fun verify(verifier: Verifier) {
        val isNone = token.has("none")
        val base = if(isNone) ExprType.Base.Any else ExprType.Base.Bool
        exprType = ExprType(base, optional = isNone)
    }
}


data class ParenExpr(val expr: Node): Node() {
    override val parts: Parts = listOf(expr)

    companion object: Node.CompanionObject {
        override fun construct(parser: Parser): Node {
            parser.expectingHas("(")
            parser.skipNewline()
            val expr = Expr.construct(parser)
            parser.skipNewline()
            parser.expectingHas(")")

            return ParenExpr(expr)
        }
    }

    override fun verify(verifier: Verifier) {
        exprType = expr.exprType
    }
}
