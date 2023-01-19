from lexer.lexer import make_tokens, LanguageError


def main():
    print("Welcome to my language")

    while True:
        line = input("> ")

        if line == "exit":
            break

        try:
            tokens = make_tokens(line)
            print(f"{tokens}")
        except LanguageError as error:
            print(error.message)


if __name__ == "__main__":
    main()
