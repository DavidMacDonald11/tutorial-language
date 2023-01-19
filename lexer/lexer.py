from token.token import Token


class LanguageError(RuntimeError):
    def __init__(self, message):
        self.message = message


class SymbolError(LanguageError):
    pass


class LexerError(LanguageError):
    pass


def make_tokens(line):
    tokens = []

    while len(line) > 0:
        char = line[0]

        if char.isspace():
            line = line[1:]
            continue

        if char in "+-*/":
            tokens += [Token(char, "Operator")]
            line = line[1:]
            continue

        if char.isdigit():
            line, token = make_number(line)
            tokens += [token]
            continue

        raise SymbolError(f"Symbol '{char}' is unrecognized")

    return tokens


def make_number(line):
    string = ""

    while len(line) > 0 and (line[0].isdigit() or line[0] == "."):
        string += line[0]
        line = line[1:]

    if len(string.split(".")) > 2:
        raise LexerError(f"A number can have, at most, 1 decimal place: '{string}'")

    token = Token(string, "Number")
    return line, token
