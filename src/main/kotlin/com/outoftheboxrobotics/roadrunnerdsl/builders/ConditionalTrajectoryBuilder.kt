package com.outoftheboxrobotics.roadrunnerdsl.builders

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.roadrunnerdsl.AutonomousBuilder
import com.outoftheboxrobotics.roadrunnerdsl.RoadrunnerDslMarker
import com.outoftheboxrobotics.roadrunnerdsl.routines.MotionRoutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

@RoadrunnerDslMarker
class ConditionalTrajectoryBuilder internal constructor(private val getBuilder: () -> AutonomousBuilder) {
    private inner class ConditionalTask(val condition: () -> Boolean, body: AutonomousBuilder.() -> Unit) {
        val builder = getBuilder().apply(body)
    }

    private val conditionalTasks = mutableListOf<ConditionalTask>()
    private var defaultTask: AutonomousBuilder.() -> Unit = {}

    fun condition(condition: () -> Boolean, body: AutonomousBuilder.() -> Unit) {
        conditionalTasks.add(ConditionalTask(condition, body))
    }

    fun default(body: AutonomousBuilder.() -> Unit) {
        defaultTask = body
    }

    internal val motionRoutine: MotionRoutine get() {
        val defaultAutonomousBuilder = getBuilder()
        val defaultEndPose = defaultAutonomousBuilder.endPose
        defaultAutonomousBuilder.apply(defaultTask)

        return object : MotionRoutine {
            override val endPose: Pose2d? = conditionalTasks
                .map { it.builder.endPose }
                .let { if (it.isEmpty() && defaultEndPose != defaultAutonomousBuilder.endPose) null else it }
                ?.all { it == defaultAutonomousBuilder.endPose }
                .let { if (it == true) defaultAutonomousBuilder.endPose else null }

            override suspend fun CoroutineScope.runTask() = coroutineScope {
                conditionalTasks.forEach {
                    if (it.condition()) {
                        with(it.builder) { runTask() }
                        return@coroutineScope
                    }
                }

                with(defaultAutonomousBuilder) { runTask() }
            }
        }
    }
}