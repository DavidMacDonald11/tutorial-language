from ..node import Node
from .additive_expression import AdditiveExpression

class Expression(Node):
    @classmethod
    def construct(cls, parser) -> Node:
        return AdditiveExpression.construct(parser)
