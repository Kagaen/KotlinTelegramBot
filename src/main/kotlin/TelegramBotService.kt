import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(
    private val botToken: String,
    private val url: String = "https://api.telegram.org/bot",
    private val client: HttpClient = HttpClient.newBuilder().build(),
) {

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$url$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(json: Json, chatId: Long, text: String): String {
        val urlSendMessage = "$url$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = text,
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(json: Json, chatId: Long): String {
        val urlSendMessage = "$url$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                inlineKeyboard = listOf(
                    listOf(
                        Button(text = "Учить слова", callbackData = LEARN_CLICK),
                        Button(text = "Статистика", callbackData = STAT_CLICK),
                    ),
                    listOf(
                        Button(text = "Сбросить прогресс", callbackData = RESET_CLICK)
                    ),
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(json: Json, chatId: Long, question: Question): String {
        val urlSendMessage = "$url$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctWord.original,
            replyMarkup = ReplyMarkup(
                inlineKeyboard = listOf(
                    question.variants.mapIndexed { index, word ->
                        Button(text = word.translate, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index")
                    },
                    listOf(
                        Button(text = "Вернуться в меню", callbackData = RETURN_CLICK)
                    ),
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}