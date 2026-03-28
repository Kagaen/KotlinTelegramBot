import java.io.File

const val MIN_LEARNED_COUNT = 3
const val DICTIONARY_NAME = "words.txt"
const val CHOICE_COUNT = 4

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int,
)

data class Question(
    val variants: List<Word>,
    val correctWord: Word,
)

data class Statistics(
    val wordsTotalCount: Int,
    val learnedCount: Int,
    val percent: Int
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary: MutableList<Word> = loadDictionary()

    fun getStatistics(): Statistics {
        val wordsTotalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= MIN_LEARNED_COUNT }.size
        val percent = if (wordsTotalCount > 0) learnedCount * 100 / wordsTotalCount else 0
        return Statistics(wordsTotalCount, learnedCount, percent)
    }

    fun getNewQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < MIN_LEARNED_COUNT }
        if (notLearnedList.isEmpty()) return null
        val questionWords = notLearnedList.shuffled().take(CHOICE_COUNT)
        val correctAnswer = questionWords.random()
        question = Question(
            variants = questionWords,
            correctWord = correctAnswer
        )
        return question
    }

    fun checkAnswer(userAnswerInput: Int): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctWord)
            if (userAnswerInput == correctAnswerId) {
                it.correctWord.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): MutableList<Word> {
        val wordsFile = File(DICTIONARY_NAME)
        val dictionary = mutableListOf<Word>()
        try {
            val stringList = wordsFile.readLines()
            stringList.forEach {
                val split = it.split("|")
                val line = Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
                dictionary.add(line)
            }
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: MutableList<Word>) {
        val wordsFile = File(DICTIONARY_NAME)
        wordsFile.writeText(dictionary.joinToString("\n") { "${it.original}|${it.translate}|${it.correctAnswersCount}" })
    }
}