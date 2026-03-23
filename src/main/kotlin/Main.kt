import java.io.File

const val minLearnedCount = 3

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
                val learnedCount = dictionary.filter().size
                val percent = learnedCount * 100 / wordsTotalCount
                println(
                    """
            Ваши успехи:
            Выучено $learnedCount из $wordsTotalCount | $percent%
            
            """.trimIndent()
                )
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
    val wordsFile = File("words.txt")
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

fun MutableList<Word>.filter(): List<Word> {
    val learnedCount = mutableListOf<Word>()
    this.forEach {
        if (it.correctAnswersCount >= minLearnedCount) learnedCount.add(it)
    }
    return learnedCount
}
