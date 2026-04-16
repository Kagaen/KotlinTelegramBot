fun main(args: Array<String>) {

    val botToken = if (args.isNotEmpty()) args[0] else ""
    val service = TelegramBotService(botToken)
    var updatesId = 0
    val updateIdRegex: Regex = "\"update_id\":\\s*(\\d+)".toRegex()
    val textRegex: Regex = "\"text\":\"([^\"]+)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":\\s*(\\d+)".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = service.getUpdates(updatesId)
        println(updates)

        val updateId = updateIdRegex.findAll(updates).lastOrNull()?.groupValues[1]
        val text = textRegex.find(updates)?.groupValues[1]
        val chatId = chatIdRegex.find(updates)?.groupValues[1]

        println(text)
        println(updateId + "\n")

        updateId?.toInt()?.let { updatesId = it + 1 }
        if (text == "Hello") service.sendMessage(chatId, "Hello")
    }
}



