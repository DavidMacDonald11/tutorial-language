from .unary_expression import UnaryExpression
from ..node import Node, BinaryNode

class MultiplicativeExpression(BinaryNode):
    @classmethod
    def construct(cls, parser) -> Node:
        return cls.construct_binary(parser, cls, UnaryExpression, ["*", "/"])