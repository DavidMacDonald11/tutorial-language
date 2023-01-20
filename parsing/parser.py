from util.error import LanguageError
from .expressions.expression import Expression
from .node import Node
from lexing.token import Token

class ParserError(LanguageError):
    pass

class Parser:
    @property
    def expression(self):
        return Expression

    def __init__(self, tokens: list[Token]):
        self.tokens = tokens
        self.i = 0

    def next(self) -> Token:
        return self.tokens[self.i]

    def take(self) -> Token:
        token = self.next()
        self.i += 1
        return token

    def expecting_has(self, *args: str):
        if self.next().has(*args):
            return self.take()

        raise ParserError(f"Expecting {args}")

    def expecting_of(self, *args: str):
        if self.next().of(*args):
            return self.take()

        raise ParserError(f"Expecting {args}")

    def make_tree(self) -> Node:
        node = Expression.construct(self)

        if not self.next().has("EOF"):
            raise ParserError(f"Unexpected token {self.next()}")

        return node
