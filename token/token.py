class Token:
    def __init__(self, string, kind):
        self.string = string
        self.kind = kind

    def __repr__(self):
        return f"{self.kind[0]}'{self.string}'"
    