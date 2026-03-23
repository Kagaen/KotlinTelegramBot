import java.io.File

fun main() {

    val wordsFile: File = File("words.txt")
    val dictionary = mutableListOf<Word>()
    try {
        val stringList = wordsFile.readLines()
        stringList.forEach {
            val split = it.split("|")
            val line: Word = Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
            dictionary.add(line)
        }
        dictionary.forEach { line -> println(line) }
    } catch (e: Exception) {
        println("Ошибка: ${e.message}")
    }

}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int,
)

