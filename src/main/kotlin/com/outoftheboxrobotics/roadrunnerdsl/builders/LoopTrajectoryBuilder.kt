package com.outoftheboxrobotics.roadrunnerdsl.builders

import com.outoftheboxrobotics.roadrunnerdsl.AutonomousBuilder
import com.outoftheboxrobotics.roadrunnerdsl.routines.MotionRoutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

class LoopTrajectoryBuilder internal constructor(private val builder: AutonomousBuilder) {
    private var condition: (() -> Boolean)? = null
    private var loopBody: AutonomousBuilder.() -> Unit = {}

    fun condition(condition: () -> Boolean) {
        this.condition = condition
    }

    fun body(loopBody: AutonomousBuilder.() -> Unit) {
        this.loopBody = loopBody
    }

    internal val motionRoutine: MotionRoutine get() {
        val startPose = builder.endPose
        val loopBuilder = builder.apply(loopBody)

        requireNotNull(condition) { "Loop Trajectory condition not set" }
        require(startPose == loopBuilder.endPose) {
            "Loop builder start pose $startPose and end pose ${loopBuilder.endPose} different"
        }

        return object : MotionRoutine {
            override val endPose = startPose

            override suspend fun CoroutineScope.runTask() = coroutineScope {
                while (condition!!()) {
                    with(loopBuilder) {
                        runTask()
                    }
                }
            }
        }
    }
}