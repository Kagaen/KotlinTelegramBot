import java.io.File

fun main() {

    val words: File = File("words.txt")
    val stringList = words.readLines()
    stringList.forEach { println(it) }

}