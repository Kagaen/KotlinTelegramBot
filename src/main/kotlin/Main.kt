import java.io.File

fun main() {

    val words: File = File("words.txt")
    try {
        val stringList = words.readLines()
        stringList.forEach { println(it) }
    } catch (e: Exception) {
        println("Ошибка: ${e.message}")
    }

}