package com.arxivai.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * On-device AI summarization engine using TextRank algorithm.
 * No external API or ML model needed - runs entirely on-device.
 * Provides extractive summarization of arXiv paper abstracts.
 */
class Summarizer {

    companion object {
        private const val DEFAULT_SUMMARY_SENTENCES = 4
        private const val DAMPING_FACTOR = 0.85
        private const val CONVERGENCE_THRESHOLD = 0.0001
        private const val MAX_ITERATIONS = 100
    }

    /**
     * Summarize a text to a given number of sentences.
     * Uses TextRank algorithm - a graph-based ranking model for text.
     */
    suspend fun summarize(
        text: String,
        maxSentences: Int = DEFAULT_SUMMARY_SENTENCES
    ): String = withContext(Dispatchers.Default) {
        if (text.isBlank()) return@withContext ""

        val sentences = splitSentences(text)
        if (sentences.size <= maxSentences) return@withContext text

        val cleanedSentences = sentences.map { cleanSentence(it) }
        val rankedSentences = textRank(cleanedSentences)

        // Select top-N sentences while preserving original order
        val topIndices = rankedSentences
            .mapIndexed { index, score -> index to score }
            .sortedByDescending { it.second }
            .take(maxSentences)
            .map { it.first }
            .sorted()

        topIndices.map { sentences[it] }.joinToString(" ")
    }

    /**
     * Generate a very short one-line tl;dr summary.
     */
    suspend fun generateTldr(text: String): String = withContext(Dispatchers.Default) {
        val summary = summarize(text, maxSentences = 2)
        if (summary.length > 200) summary.take(197) + "..." else summary
    }

    /**
     * Extract key phrases/topics from text.
     */
    suspend fun extractKeywords(text: String, maxKeywords: Int = 5): List<String> =
        withContext(Dispatchers.Default) {
            val stopWords = setOf(
                "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
                "of", "by", "with", "from", "as", "is", "was", "are", "were", "be",
                "been", "being", "have", "has", "had", "do", "does", "did", "will",
                "would", "could", "should", "may", "might", "shall", "can", "need",
                "this", "that", "these", "those", "it", "its", "we", "our", "they",
                "their", "them", "which", "what", "who", "whom", "when", "where",
                "why", "how", "all", "each", "every", "both", "few", "more", "most",
                "other", "some", "such", "no", "nor", "not", "only", "own", "same",
                "so", "than", "too", "very", "just", "because", "about", "also",
                "into", "over", "after", "before", "between", "under", "above",
                "below", "up", "down", "out", "off", "again", "further", "then",
                "once", "here", "there", "when", "where", "why", "how", "during",
                "without", "through", "within", "along", "around", "among", "across",
                "behind", "beyond", "toward", "upon", "via", "while", "since",
                "until", "although", "though", "if", "because", "before", "after",
                "while", "whereas", "whether", "either", "neither", "who", "whom",
                "whose", "which", "that", "what", "whatever", "whenever", "wherever",
                "however", "moreover", "furthermore", "nevertheless", "nonetheless",
                "therefore", "accordingly", "consequently", "hence", "thus", "hereby",
                "herein", "thereby", "therein", "meanwhile", "afterwards", "beforehand",
                "etc", "eg", "ie", "al", "et", "using", "based", "paper", "method",
                "approach", "results", "show", "propose", "present", "introduce",
                "experiment", "performance", "model", "proposed", "new", "two",
                "different", "one", "first", "also", "well", "however", "although",
                "due", "used", "set", "data", "given", "order", "obtain", "obtained",
                "shown", "demonstrate", "demonstrated", "significant", "significantly",
                "better", "best", "state-of-the-art", "state", "art", "task",
                "tasks", "problem", "problems", "work", "works", "existing",
                "previous", "prior", "recent", "recently", "current", "currently",
                "future", "potential", "possible", "typically", "often", "commonly",
                "generally", "usually", "well-known", "known", "widely", "broadly",
                "instead", "rather", "alternative", "alternatively", "however",
                "though", "despite", "spite", "regardless", "nonetheless",
                "nevertheless", "notably", "particularly", "specifically", "especially",
                "importantly", "interestingly", "remarkably", "surprisingly"
            )

            val words = text.lowercase()
                .replace(Regex("[^a-z0-9\\s-]"), " ")
                .split(Regex("\\s+"))
                .filter { it.length > 2 && it !in stopWords }

            val wordFreq = words.groupingBy { it }.eachCount()
                .filter { it.value > 1 }
                .toList()
                .sortedByDescending { it.second }
                .take(maxKeywords)
                .map { it.first }

            wordFreq
        }

    // --- Private implementation ---

    private fun splitSentences(text: String): List<String> {
        // Split on sentence boundaries, handling abbreviations
        val sentenceEndings = Regex("(?<=[.!?])\\s+(?=[A-Z\"'(\\[{])")
        val sentences = text.split(sentenceEndings)
            .map { it.trim() }
            .filter { it.length > 20 && it.count { c -> c in ".!?" } > 0 }

        return if (sentences.isEmpty()) {
            // Fallback: split by periods
            text.split(".")
                .map { it.trim() + "." }
                .filter { it.length > 20 }
        } else {
            sentences
        }
    }

    private fun cleanSentence(sentence: String): String {
        return sentence
            .lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun textRank(sentences: List<String>): List<Double> {
        val n = sentences.size
        if (n == 0) return emptyList()
        if (n == 1) return listOf(1.0)

        // Build similarity matrix using cosine similarity
        val similarityMatrix = Array(n) { i ->
            DoubleArray(n) { j ->
                if (i == j) 0.0
                else cosineSimilarity(sentences[i], sentences[j])
            }
        }

        // Normalize the matrix (row-wise)
        for (i in 0 until n) {
            val rowSum = similarityMatrix[i].sum()
            if (rowSum > 0) {
                for (j in 0 until n) {
                    similarityMatrix[i][j] /= rowSum
                }
            }
        }

        // Initialize scores to 1.0
        val scores = DoubleArray(n) { 1.0 }
        val previousScores = DoubleArray(n)

        // Iterate until convergence
        for (iteration in 0 until MAX_ITERATIONS) {
            System.arraycopy(scores, 0, previousScores, 0, n)

            for (i in 0 until n) {
                var sum = 0.0
                for (j in 0 until n) {
                    sum += similarityMatrix[j][i] * previousScores[j]
                }
                scores[i] = (1 - DAMPING_FACTOR) + DAMPING_FACTOR * sum
            }

            // Check convergence
            var diff = 0.0
            for (i in 0 until n) {
                diff += abs(scores[i] - previousScores[i])
            }
            if (diff < CONVERGENCE_THRESHOLD) break
        }

        return scores.toList()
    }

    private fun cosineSimilarity(s1: String, s2: String): Double {
        val words1 = s1.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val words2 = s2.split(Regex("\\s+")).filter { it.isNotEmpty() }

        if (words1.isEmpty() || words2.isEmpty()) return 0.0

        val allWords = (words1 + words2).distinct()
        val vec1 = allWords.map { w -> words1.count { it == w }.toDouble() }
        val vec2 = allWords.map { w -> words2.count { it == w }.toDouble() }

        val dotProduct = vec1.zip(vec2).sumOf { it.first * it.second }
        val norm1 = sqrt(vec1.sumOf { it * it })
        val norm2 = sqrt(vec2.sumOf { it * it })

        if (norm1 == 0.0 || norm2 == 0.0) return 0.0

        return dotProduct / (norm1 * norm2)
    }

    private fun abs(x: Double) = if (x < 0) -x else x
}