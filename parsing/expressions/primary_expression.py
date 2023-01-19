from ..node import Node, PrimaryNode

class PrimaryExpression(PrimaryNode):
    @classmethod
    def construct(cls, parser) -> Node:
        if not parser.next().has("("):
            token = parser.expecting_of("Number")
            return PrimaryExpression(token)

        parser.take()
        node = parser.expression.construct(parser)
        parser.expecting_has(")")

        return node


