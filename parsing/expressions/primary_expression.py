from ..node import Node, PrimaryNode

class PrimaryExpression(PrimaryNode):
    @classmethod
    def construct(cls, parser) -> Node:
        token = parser.expecting_of("Number")
        return PrimaryExpression(token)
