import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val STAT_CLICK = "statistics_clicked"
const val LEARN_CLICK = "learn_words_clicked"
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
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

fun main(args: Array<String>) {

    val botToken = if (args.isNotEmpty()) args[0] else ""
    val service = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()
    var lastUpdateId = 0L

    val json = Json {
        ignoreUnknownKeys = true
    }

    while (true) {
        Thread.sleep(2000)
        val responseString: String = service.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString<Response>(responseString)
        val updates = response.result
        val firstUpdate: Update = updates.firstOrNull() ?: continue
        val updateId: Long = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val text = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data


        if (chatId != null) {
            when {
                text?.lowercase() == "/start" -> service.sendMenu(json, chatId)

                data == STAT_CLICK -> {
                    val statistics = trainer.getStatistics()
                    service.sendMessage(
                        json,
                        chatId,
                        "Выучено ${statistics.learnedCount} из ${statistics.wordsTotalCount} | ${statistics.percent}%"
                    )
                }

                data == LEARN_CLICK -> checkNextQuestionAndSend(json, trainer, service, chatId)

                data != null && data.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> {
                    trainer.question?.let {
                        val userAnswerInput: Int = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
                        if (trainer.checkAnswer(userAnswerInput)) {
                            service.sendMessage(json, chatId, "Правильно!")
                            checkNextQuestionAndSend(json, trainer, service, chatId)
                        } else {
                            service.sendMessage(
                                json,
                                chatId,
                                "Неправильно! ${it.correctWord.original} - это ${it.correctWord.translate}"
                            )
                            checkNextQuestionAndSend(json, trainer, service, chatId)
                        }
                    }
                }
            }
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



