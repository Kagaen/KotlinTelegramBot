fun main() {

    val trainer = LearnWordsTrainer()
    while (true) {
        println("1.Учить слова\n2.Статистика\n0.Выход")
        when (readln().trim()) {
            "1" -> {
                outer@ while (true) {
                    val question = trainer.getNewQuestion()
                    if (question == null) {
                        println("Все слова в словаре выучены\n")
                        break
                    }
                    while (true) {
                        println(question.asConsoleString())
                        try {
                            val userAnswerInput: Int = readln().trim().toInt()
                            if (userAnswerInput !in 0..question.variants.size) {
                                println("Выберите вариант ответа")
                                continue
                            }
                            if (userAnswerInput == 0) break@outer
                            if (trainer.checkAnswer(userAnswerInput.minus(1))) {
                                println("Правильно!")
                                continue@outer
                            } else {
                                println("Неправильно! ${question.correctWord.original} - это ${question.correctWord.translate}")
                                continue@outer
                            }
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }
                }
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.wordsTotalCount} | ${statistics.percent}%\n")
            }

            "0" -> return

            else -> println("Введите номер действия\n")
        }
    }

}

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index, word -> " ${index + 1}.${word.translate}" }
        .joinToString("\n")
    return "${this.correctWord.original}:\n$variants\n ----------\n 0 - Меню"
}