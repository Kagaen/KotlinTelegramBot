import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = if (args.isNotEmpty()) args[0] else ""
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"

    val client: HttpClient = HttpClient.newBuilder().build()

    val requestMe: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val responseMe: HttpResponse<String> = client.send(requestMe, HttpResponse.BodyHandlers.ofString())

    val requestUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseUpdates: HttpResponse<String> = client.send(requestUpdates, HttpResponse.BodyHandlers.ofString())
    println(responseMe.body() + "\n\n" + responseUpdates.body())

}
