import java.io.File

fun main() {

    val words: File = File("src\\main\\kotlin\\words.txt")
    val stringList = words.readLines()
    repeat(stringList.size) { str: Int ->
        println(stringList[str])
    }

}