package io.github.funofprograming.pr.rule.loop

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

data class LoopState(
    val loopLabel:String?,
    val maxIterations:Int?,
    val currentIteration: AtomicInteger,
    val continueNextIteration: AtomicBoolean
)