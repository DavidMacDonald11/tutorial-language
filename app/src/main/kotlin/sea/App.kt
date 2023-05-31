package sea

import kotlinx.coroutines.*
import sea.lexer.*

fun main(args: Array<String>) = runBlocking {
    val options = args[0]
    val outDir = args[1]
    val filePaths = args.drop(2)

    val jobs = ArrayList<Job>(filePaths.size)

    for(srcPath in filePaths) {
        val job = launch{compileFile(srcPath, outDir, options)}
        jobs.add(job)
    }

    jobs.joinAll()
}

suspend fun compileFile(srcPath: String, outDir: String, options: String) {
    val dFile = DebugFile(srcPath, outDir, options)
    val faults = Faults()

    val lexer = Lexer(faults, srcPath)
    if(runLexer(lexer, dFile)) return

    // Run Stage 2 (parser)

    if(faults.warnings.size > 0) println("$faults")
}

fun runLexer(lexer: Lexer, dFile: DebugFile) = runStage(lexer.faults) {
    println("Building ${lexer.file.path}...")
    lexer.makeTokens()
    dFile.write("Tokens:\n\t$lexer")
}

fun runStage(faults: Faults, func: () -> Unit): Boolean {
    var failed = false

    try {
        func()
        faults.finishStage()
    } catch(e: Faults.CompilerFailure) {
        failed = true
        println("$faults")
    }

    return failed
}
