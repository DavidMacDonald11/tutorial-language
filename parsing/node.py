from lexing.token import Token

class Node:
    @classmethod
    def construct(cls, parser):
        pass

class PrimaryNode(Node):
    def __init__(self, token: Token):
        self.token = token

class BinaryNode(Node):
    def __init__(self, left: Node, op: Token, right: Node):
        self.left = left
        self.op = op
        self.right = right

    @classmethod
    def construct_binary(cls, parser, mine, other, ops: list[str]):
        node = other.construct(parser)

        while parser.next().has(*ops):
            op = parser.take()
            right = other.construct(parser)
            node = mine(node, op, right)

        return node
