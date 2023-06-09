package sea

import sea.files.SourceLine

class Faults {
    class CompilerFailure: Exception() {}

    public interface Component {
        val lines: List<SourceLine>
        val markedLines get() = lines.map{it.markedLine}.joinToString()
        fun treeString(prefix: String): String
        fun mark()
    }

    var stageVerb = ""
    val warnings = mutableListOf<String>()
    val errors = mutableListOf<String>()
    var failure: String? = null

    override fun toString(): String {
        var string = ""

        if(warnings.size > 0) string += warnings.joinToString("\n") + "\n"
        if(errors.size > 0) string += errors.joinToString("\n") + "\n"
        string += failure?.plus("\n")?: ""

        return string
    }

    private fun act(c: Component, message: String, severity: String): String {
        c.mark()
        val label = "$stageVerb $severity"
        return "$label: $message\n${c.markedLines}"
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
        if(errors.size == 0) return
        throw CompilerFailure()
    }
}
