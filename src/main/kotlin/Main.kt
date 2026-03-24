import java.io.File

const val MIN_LEARNED_COUNT = 3
const val DICTIONARY_NAME = "words.txt"

fun main() {
    val dictionary: MutableList<Word> = loadDictionary()
    val wordsTotalCount: Int = dictionary.size
    while (true) {
        println(
            """             
        1. Учить слова 
        2. Статистика
        0. Выход
        """.trimIndent()
        )
        when (readln().trim()) {
            "1" -> {
                println("Учим слова...\n")
                continue
            }

            "2" -> {
                val learnedCount = dictionary.filter { it.correctAnswersCount >= MIN_LEARNED_COUNT }.size
                val percent = if (wordsTotalCount > 0) learnedCount * 100 / wordsTotalCount else 0
                println("Выучено $learnedCount из $wordsTotalCount | $percent%\n")
                continue
            }

            "0" -> return
            else -> {
                println("Введите номер действия\n")
                continue
            }
        }
    }

}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int,
)

fun loadDictionary(): MutableList<Word> {
    val wordsFile = File(DICTIONARY_NAME)
    val dictionary = mutableListOf<Word>()
    try {
        val stringList = wordsFile.readLines()
        stringList.forEach {
            val split = it.split("|")
            val line = Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
            dictionary.add(line)
        }
    } catch (e: Exception) {
        println("Ошибка: ${e.message}")
    }
    return dictionary
}
