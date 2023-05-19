package sea

class Faults {
    class CompilerFailure: Exception() {}

    public interface Component {
        fun treeString(prefix: String): String
        fun mark()
        fun getMarkedLines(): String
    }

    var stage = 0
    val warnings = mutableListOf<String>()
    val errors = mutableListOf<String>()
    var failure: String? = null

    companion object {
        val stageMap = arrayOf("Lexing", "Parsing", "Transpiling")
    }

    override fun toString(): String {
        var string = ""

        string += warnings.joinToString("\n")
        string += errors.joinToString("\n")
        string += if(failure == null) "" else "\n$failure\n"

        return string
    }

    private fun act(c: Component, message: String, severity: String): String {
        c.mark()
        val label = "${stageMap[stage]} $severity"
        return "$label: $message\n${c.getMarkedLines()}"
    }

    fun warn(c: Component, message: String) {
        warnings.add(act(c, message, "Warning"))
    }

    fun error(c: Component, message: String) {
        errors.add(act(c, message, "Error"))
    }

    fun fail(c: Component, message: String): CompilerFailure {
        failure = act(c, message, "Failure")
        return CompilerFailure()
    }

    fun finishStage() {
        stage += 1
        if(errors.size == 0) return
        throw CompilerFailure()
    }
}
