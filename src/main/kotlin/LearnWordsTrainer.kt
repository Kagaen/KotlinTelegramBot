import java.io.File

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

class LearnWordsTrainer(
    private val dictionaryName: String = "words.txt",
    private val minLearnedCount: Int = 3,
    private val variantCount: Int = 4,
) {

    var question: Question? = null
        private set
    private val dictionary: MutableList<Word> = loadDictionary()

    fun getStatistics(): Statistics {
        val wordsTotalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= minLearnedCount }.size
        val percent = if (wordsTotalCount > 0) learnedCount * 100 / wordsTotalCount else 0
        return Statistics(wordsTotalCount, learnedCount, percent)
    }

    fun getNewQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < minLearnedCount }
        if (notLearnedList.isEmpty()) return null
        val questionWords: List<Word>
        val correctAnswer: Word
        if (notLearnedList.size < variantCount) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= minLearnedCount }.shuffled()
            questionWords =
                learnedList.take(variantCount - notLearnedList.size) + notLearnedList.shuffled()
                    .take(notLearnedList.size)
            correctAnswer = notLearnedList.random()
        } else {
            questionWords = notLearnedList.shuffled().take(variantCount)
            correctAnswer = questionWords.random()
        }
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
                saveDictionary()
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): MutableList<Word> {
        val wordsFile = File(dictionaryName)
        if (!wordsFile.exists()) {
            File("words.txt").copyTo(wordsFile)
        }
        val dictionary = mutableListOf<Word>()
        try {
            wordsFile.readLines().forEach {
                val split = it.split("|")
                val line = Word(split[0], split[1], split[2].toIntOrNull() ?: 0)
                dictionary.add(line)
            }
            return dictionary
        } catch (_: Exception) {
            throw IllegalStateException("Невозможно загрузить словарь")
        }
    }

    private fun saveDictionary() {
        val wordsFile = File(dictionaryName)
        wordsFile.writeText(dictionary.joinToString("\n") { "${it.original}|${it.translate}|${it.correctAnswersCount}" })
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}