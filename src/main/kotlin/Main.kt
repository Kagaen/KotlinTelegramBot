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
                while (true) {
                    var notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_LEARNED_COUNT }

                    if (notLearnedList.isEmpty()) {
                        println("Все слова в словаре выучены\n")
                        break
                    }

                    val questionWords = notLearnedList.shuffled().take(4)
                    val correctAnswer = (0..<questionWords.size).random()
                    val askWord = questionWords[correctAnswer]

                    while (true) {
                        try {
                            println("\n${askWord.original}:")
                            questionWords.forEachIndexed { index, word -> println("${index + 1}. ${word.translate}") }
                            val userAnswer = readln().trim().toInt()
                            if (userAnswer in 1..questionWords.size) break // тут будет проверка верного ответа
                            else println("Введите вариант ответа")
                        } catch (e: Exception) {
                            println("Ошибка: ${e.message}")
                        }
                    }
                    return
                }
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
    var correctAnswersCount: Int,
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
