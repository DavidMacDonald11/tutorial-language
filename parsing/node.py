from lexing.token import Token

class Node:
    @classmethod
    def construct(cls, parser):
        pass

    def __repr__(self):
        return self.tree_repr()

    def tree_repr(self, prefix: str = "    "):
        string = type(self).__name__
        nodes = self.nodes()

        for i, node in enumerate(nodes):
            at_last = (i == len(nodes) - 1)
            symbol = "└──" if at_last else "├──"
            prefix_symbol = "" if at_last else "│"

            node_string = node.tree_repr(f"{prefix}{prefix_symbol}    ")
            string += f"\n{prefix}{symbol} {node_string}"

        return string


    def nodes(self) -> list:
        pass

class PrimaryNode(Node):
    def __init__(self, token: Token):
        self.token = token

    def tree_repr(self, prefix: str = "    "):
        return f"{type(self).__name__} ── {self.token}"

    def nodes(self) -> list:
        return [self.token]

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

    def nodes(self) -> list:
        return [self.left, self.op, self.right]
