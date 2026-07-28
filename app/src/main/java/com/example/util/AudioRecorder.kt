package com.example.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

object AudioRecorder {
    fun startSimulatedRecording(): Flow<RecordState> = flow {
        var elapsedSec = 0
        while (true) {
            val amplitudes = List(20) { Random.nextFloat() * 0.8f + 0.2f }
            emit(RecordState(seconds = elapsedSec, amplitudes = amplitudes))
            delay(1000)
            elapsedSec++
        }
    }

    data class RecordState(
        val seconds: Int,
        val amplitudes: List<Float>
    )
}
