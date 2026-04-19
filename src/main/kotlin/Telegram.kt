const val STAT_CLICK = "statistics_clicked"
const val LEARN_CLICK = "learn_words_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

fun main(args: Array<String>) {

    val botToken = if (args.isNotEmpty()) args[0] else ""
    val service = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()
    var lastUpdateId = 0

    val updateIdRegex: Regex = "\"update_id\":\\s*(\\d+)".toRegex()
    val textRegex: Regex = "\"text\":\"([^\"]+)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":\\s*(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"([^\"]+)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = service.getUpdates(lastUpdateId)
        println(updates)

        val updateId = updateIdRegex.findAll(updates).lastOrNull()?.groupValues[1]
        val text = textRegex.find(updates)?.groupValues[1]
        val chatId = chatIdRegex.find(updates)?.groupValues[1]
        val data = dataRegex.find(updates)?.groupValues[1]

        updateId?.toInt()?.let { lastUpdateId = it + 1 }

        if (text?.lowercase() == "/start" && chatId != null) service.sendMenu(chatId)
        if (data == STAT_CLICK && chatId != null) {
            val statistics = trainer.getStatistics()
            service.sendMessage(
                chatId,
                "Выучено ${statistics.learnedCount} из ${statistics.wordsTotalCount} | ${statistics.percent}%"
            )
        }
        if (data == LEARN_CLICK && chatId != null) checkNextQuestionAndSend(trainer, service, chatId)
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    service: TelegramBotService,
    chatId: String,
): String {
    val question: Question? = trainer.getNewQuestion()
    return if (question == null) service.sendMessage(chatId, "Все слова в словаре выучены")
    else service.sendQuestion(chatId, question)
}



