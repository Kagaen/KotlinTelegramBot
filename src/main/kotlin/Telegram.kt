import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val URL = "https://api.telegram.org/bot"

fun main(args: Array<String>) {

    val botToken = if (args.isNotEmpty()) args[0] else ""
    var updatesId = 0
    val updateIdRegex: Regex = "\"update_id\":\\s*(\\d+)".toRegex()
    val textRegex: Regex = "\"text\":\"([^\"]+)\"".toRegex()
    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updatesId)
        println(updates)

        val updateIdMatchResult: Sequence<MatchResult> = updateIdRegex.findAll(updates)
        val updateIdString: String? = updateIdMatchResult.lastOrNull()?.groupValues[1]
        val textMatchResult: MatchResult? = textRegex.find(updates)
        val text = textMatchResult?.groupValues[1]
        println(text)
        println(updateIdString+"\n")

        updateIdString?.toInt()?.let { updatesId = it + 1 }
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "$URL$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}
