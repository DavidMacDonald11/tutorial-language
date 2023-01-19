from lexing.lexer import make_tokens, LanguageError
from parsing.parser import Parser

def main():
    print("Welcome to my language")

    while True:
        line = input("> ")

        if line == "exit":
            break

        try:
            tokens = make_tokens(line)

            parser = Parser(tokens)
            tree = parser.make_tree()

            print(tree)
        except LanguageError as error:
            print(error.message)

if __name__ == "__main__":
    main()
