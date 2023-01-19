from lexing.token import Token
from util.error import LanguageError

class LexerError(LanguageError):
    pass

class Lexer:
    def __init__(self, line: str):
        self.line = line
        self.i = 0

    def __len__(self):
        return len(self.line) - self.i

    def next(self) -> str:
        return self.line[self.i] if self.i < len(self.line) else ""

    def take(self) -> str:
        char = self.next()
        self.i += 1 if self.i < len(self.line) else 0
        return char

    def make_tokens(self) -> list[Token]:
        tokens = []

        while len(self) > 0:
            tokens += [self.make_token()]

        tokens += [Token("EOF", "Punctuator")]
        return tokens

    def make_token(self) -> Token:
        while self.next().isspace():
            self.take()

        if self.next() in "()":
            return Token(self.take(), "Punctuator")

        if self.next() in "+-*/%":
            return self.make_operator()

        if self.next().isdigit() or self.next() == ".":
            return self.make_number()

        raise LexerError(f"Symbol '{self.next()}' is unrecognized")

    def make_operator(self) -> Token:
        string = self.take()

        if self.next() == "/":
            string += self.take()

        return Token(string, "Operator")

    def make_number(self) -> Token:
        string = ""

        while len(self) > 0 and (self.next().isdigit() or self.next() == "."):
            string += self.take()

        if string.count(".") > 1:
            raise LexerError(f"A number can have, at most, 1 decimal place: '{string}'")

        return Token(string, "Number")
