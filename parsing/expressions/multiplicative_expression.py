from .unary_expression import UnaryExpression
from ..node import Node, BinaryNode

class MultiplicativeExpression(BinaryNode):
    @classmethod
    def construct(cls, parser) -> Node:
        return cls.construct_binary(parser, cls, UnaryExpression, ["*", "/", "%", "//"])

    def interpret(self):
        left = self.left.interpret()
        right = self.right.interpret()

        match self.op.string:
            case "*":
                return left * right
            case "/":
                return left / right
            case "%":
                return left % right
            case "//":
                return int(left // right)
