import java.io.File

fun main() {

    val words: File = File("src\\main\\kotlin\\words.txt")
    repeat(words.readLines().size) {
        str: Int -> println(words.readLines()[str])
    }

}