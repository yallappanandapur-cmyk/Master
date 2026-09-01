package com.example.biometrics

import java.util.UUID
import kotlin.math.sqrt
import kotlin.random.Random

data class FaceEmbedding(
    val vector: FloatArray,
    val sampleQuality: Float,
    val livenessScore: Float
) {
    fun toSerializedString(): String {
        return vector.joinToString(",")
    }

    companion object {
        fun fromSerializedString(str: String): FloatArray {
            return str.split(",").mapNotNull { it.toFloatOrNull() }.toFloatArray()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FaceEmbedding
        return vector.contentEquals(other.vector)
    }

    override fun hashCode(): Int {
        return vector.contentHashCode()
    }
}

data class VerificationResult(
    val isSuccess: Boolean,
    val confidence: Float,
    val livenessScore: Float,
    val matchedTeacherId: String?,
    val message: String
)

enum class LivenessChallenge(val promptEn: String, val promptKn: String) {
    LOOK_STRAIGHT("Look directly at the camera", "ಕ್ಯಾಮೆರಾವನ್ನು ನೇರವಾಗಿ ನೋಡಿ"),
    BLINK_EYES("Please blink your eyes naturally", "ದಯವಿಟ್ಟು ಕಣ್ಣುಗಳನ್ನು ಮಿಟುಕಿಸಿ"),
    SMILE("Smile gently at the screen", "ಕ್ಯಾಮೆರಾಗೆ ಮೃದುವಾಗಿ ನಗಿರಿ"),
    TURN_SLIGHTLY("Tilt your head slightly right", "ತಲೆಯನ್ನು ಸ್ವಲ್ಪ ಬಲಕ್ಕೆ ತಿರುಗಿಸಿ")
}

object FaceRecognitionEngine {
    private const val EMBEDDING_DIMENSION = 128
    private const val MATCH_THRESHOLD = 0.82f // 82%+ Cosine similarity considered authenticated match

    /**
     * Simulates biometric feature extraction from facial landmarks / tensor model
     * Seeds with teacher identifier during registration for deterministic embedding matching
     */
    fun extractEmbedding(seedKey: String, noiseFactor: Float = 0.02f): FaceEmbedding {
        val rand = Random(seedKey.hashCode().toLong())
        val vector = FloatArray(EMBEDDING_DIMENSION)
        var normSum = 0.0f
        for (i in 0 until EMBEDDING_DIMENSION) {
            val baseVal = (rand.nextFloat() * 2f) - 1f
            val perturbed = baseVal + (Random.nextFloat() * noiseFactor * 2f - noiseFactor)
            vector[i] = perturbed
            normSum += perturbed * perturbed
        }
        // Normalize L2
        val norm = sqrt(normSum)
        for (i in 0 until EMBEDDING_DIMENSION) {
            vector[i] /= norm
        }

        val liveness = 0.96f + (Random.nextFloat() * 0.038f)
        val quality = 0.95f + (Random.nextFloat() * 0.045f)
        return FaceEmbedding(vector, quality, liveness)
    }

    /**
     * Calculates cosine similarity between two normalized embedding vectors.
     */
    fun calculateCosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.isEmpty() || v2.isEmpty() || v1.size != v2.size) return 0f
        var dot = 0f
        var n1 = 0f
        var n2 = 0f
        for (i in v1.indices) {
            dot += v1[i] * v2[i]
            n1 += v1[i] * v1[i]
            n2 += v2[i] * v2[i]
        }
        val denom = sqrt(n1) * sqrt(n2)
        if (denom == 0f) return 0f
        return (dot / denom).coerceIn(0f, 1f)
    }

    /**
     * Verifies live scan against enrolled face profile
     */
    fun verifyFace(
        liveEmbedding: FloatArray,
        storedSerializedEmbedding: String,
        targetTeacherId: String
    ): VerificationResult {
        val storedVector = FaceEmbedding.fromSerializedString(storedSerializedEmbedding)
        if (storedVector.isEmpty()) {
            return VerificationResult(
                isSuccess = false,
                confidence = 0f,
                livenessScore = 0f,
                matchedTeacherId = null,
                message = "Face profile not found on record"
            )
        }

        val similarity = calculateCosineSimilarity(liveEmbedding, storedVector)
        val liveness = 0.97f + (Random.nextFloat() * 0.025f)
        val isMatch = similarity >= MATCH_THRESHOLD

        return VerificationResult(
            isSuccess = isMatch,
            confidence = similarity,
            livenessScore = liveness,
            matchedTeacherId = if (isMatch) targetTeacherId else null,
            message = if (isMatch) "Face Verified Successfully" else "Face recognition match score below threshold (${(similarity * 100).toInt()}%)"
        )
    }
}
