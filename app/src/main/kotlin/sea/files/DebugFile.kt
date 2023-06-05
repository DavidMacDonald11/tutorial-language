package sea.files

import java.io.File

data class DebugFile(
    val srcPath: String,
    val outDir: String,
    val options: String)
{

    val path = "$outDir/${File("${srcPath}debug").name}"
    val enabled = "d" in options

    init {
       if(enabled) File(path).writeText("$srcPath\n\n")
    }

    fun write(string: String, end: String = "\n\n") {
        if(enabled) File(path).appendText("$string$end")
    }
}

