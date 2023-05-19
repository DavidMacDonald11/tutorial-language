package sea.lexer

data class SourceLine(val num: Int, val text: String) {
    data class Position(var start: Int = 0, var end: Int = 0) {}

    private var position = Position()
    private val unreadText get() = text.substring(position.end)

    val marks = mutableListOf<Position>()
    val atEnd get() = (unreadText == "")
    val nextChar get() = if(!atEnd) unreadText[0] else '\u0000'

    fun nextString(n: Int) = unreadText.take(n)
    fun ignorePosition() { position.start = position.end }
    fun markPosition(position: Position) { marks.add(position) }
    fun stepPosition() { if(position.end < text.length) position.end += 1 }

    fun newPosition(): Position {
        val position = position.copy()
        ignorePosition()
        return position
    }

    fun newString(position: Position): String {
        return text.substring(position.start, position.end)
    }

    fun getMarkedLine(): String {
        var start = -1
        var end = -1

        for(position in marks) {
            if(start == -1 || position.start < start) start = position.start
            if(end == -1 || position.end > end) end = position.end
        }

        if(text == "") return "${"%4d".format(num)}|EOF\n    |^^^"

        var markers = ""

        for(i in text.indices) {
            markers += if(i < start || i >= end) ' ' else '^'
        }

        return "${"%4d".format(num)}|$text    |$markers"
    }
}
