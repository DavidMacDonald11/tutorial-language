from .multiplicative_expression import MultiplicativeExpression
from ..node import Node, BinaryNode

class AdditiveExpression(BinaryNode):
    @classmethod
    def construct(cls, parser) -> Node:
        return cls.construct_binary(parser, cls, MultiplicativeExpression, ["+", "-"])
