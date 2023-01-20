from lexing.lexer import Lexer, LanguageError
from parsing.parser import Parser

def main():
    print("Welcome to my language")

    while True:
        line = input("> ")

        if line == "exit":
            break

        try:
            lexer = Lexer(line)
            tokens = lexer.make_tokens()

            parser = Parser(tokens)
            tree = parser.make_tree()

            print(tree.interpret())
        except LanguageError as error:
            print(error)

if __name__ == "__main__":
    main()
