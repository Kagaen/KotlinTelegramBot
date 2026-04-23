import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val STAT_CLICK = "statistics_clicked"
const val LEARN_CLICK = "learn_words_clicked"
const val RETURN_CLICK = "return_clicked"
const val RESET_CLICK = "reset_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<Button>>,
)

@Serializable
data class Button(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

fun main(args: Array<String>) {

    val botToken = if (args.isNotEmpty()) args[0] else ""
    val service = TelegramBotService(botToken)
    var lastUpdateId = 0L
    val json = Json { ignoreUnknownKeys = true }
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)
        val responseString: String = service.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)
        if (response.result.isEmpty()) continue
        val sortUpdates = response.result.sortedBy { it.updateId }
        sortUpdates.forEach { handleUpdate(it, json, service, trainers) }
        lastUpdateId = sortUpdates.last().updateId + 1
    }
}

fun handleUpdate(update: Update, json: Json, service: TelegramBotService, trainers: HashMap<Long, LearnWordsTrainer>) {

    val text = update.message?.text
    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val data = update.callbackQuery?.data
    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    when {
        text?.lowercase() == "/start" || data == RETURN_CLICK -> service.sendMenu(json, chatId)

        data == STAT_CLICK -> {
            val statistics = trainer.getStatistics()
            service.sendMessage(
                json,
                chatId,
                "Выучено ${statistics.learnedCount} из ${statistics.wordsTotalCount} | ${statistics.percent}%"
            )
        }

        data == LEARN_CLICK -> checkNextQuestionAndSend(json, trainer, service, chatId)

        data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true -> {
            val userAnswerInput: Int = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkAnswer(userAnswerInput)) service.sendMessage(json, chatId, "Правильно!")
            else service.sendMessage(
                json,
                chatId,
                "Неправильно! ${trainer.question?.correctWord?.original} - это ${trainer.question?.correctWord?.translate}"
            )
            checkNextQuestionAndSend(json, trainer, service, chatId)
        }

        data == RESET_CLICK -> {
            trainer.resetProgress()
            service.sendMessage(json, chatId, "Прогресс сброшен")
        }
    }
}

fun checkNextQuestionAndSend(
    json: Json,
    trainer: LearnWordsTrainer,
    service: TelegramBotService,
    chatId: Long,
): String {
    val question: Question? = trainer.getNewQuestion()
    return if (question == null) service.sendMessage(json, chatId, "Все слова в словаре выучены")
    else service.sendQuestion(json, chatId, question)
}



