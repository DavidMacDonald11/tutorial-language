class Token:
    def __init__(self, string: str, kind: str):
        self.string = string
        self.kind = kind

    def __repr__(self) -> str:
        return f"{self.kind[0]}'{self.string}'"

    def has(self, *args: str) -> bool:
        return self.string in args

    def of(self, *args: str) -> bool:
        return self.kind in args
