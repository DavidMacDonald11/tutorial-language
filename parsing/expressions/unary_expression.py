from lexing.token import Token
from .primary_expression import PrimaryExpression
from ..node import Node

class UnaryExpression(Node):
    def __init__(self, op: Token, right: Node):
        self.op = op
        self.right = right

    @classmethod
    def construct(cls, parser) -> Node:
        if not parser.next().has("-", "+"):
            return PrimaryExpression.construct(parser)

        op = parser.take()
        return UnaryExpression(op, UnaryExpression.construct(parser))

    def nodes(self) -> list:
        return [self.op, self.right]

    def interpret(self):
        num = self.right.interpret()

        if self.op.string == "-":
            num *= -1

        return num
