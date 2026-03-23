import java.io.File

fun main() {
    val dictionary = loadDictionary()
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
                println("Учим слова...")
                continue
            }

            "2" -> {
                println("Ваши успехи:")
                continue
            }

            "0" -> return
            else -> {
                println("Введите номер действия")
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
