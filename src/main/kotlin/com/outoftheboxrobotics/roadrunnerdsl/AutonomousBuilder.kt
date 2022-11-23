package com.outoftheboxrobotics.roadrunnerdsl

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.outoftheboxrobotics.roadrunnerdsl.builders.ConditionalTrajectoryBuilder
import com.outoftheboxrobotics.roadrunnerdsl.builders.LoopTrajectoryBuilder
import com.outoftheboxrobotics.roadrunnerdsl.routines.*
import kotlinx.coroutines.*

@RoadrunnerDslMarker
class AutonomousBuilder(
    private val getTrajectoryBuilder: (Pose2d) -> TrajectoryBuilder,
    private val runTrajectory: (Trajectory) -> Unit,
    private val defaultPose: Pose2d = Pose2d()
) : MotionRoutine {
    private val routines = mutableListOf<AutonomousRoutine>()

    override val endPose
        get() = routines.filterIsInstance<MotionRoutine>().let {
            if (it.isEmpty()) defaultPose
            else it.last().endPose
        }

    private fun createSubroutine() = AutonomousBuilder(
        getTrajectoryBuilder,
        runTrajectory,
        requireNotNull(endPose) { "Continuation of autonomous builder after split conditional trajectory" }
    )

    fun trajectory(block: TrajectoryBuilder.() -> Unit) {
        routines.add(TrajectoryWrapper(
            getTrajectoryBuilder(
                requireNotNull(endPose) { "Continuation of autonomous builder after split conditional trajectory" }
            ).apply(block).build(),
            runTrajectory
        ))
    }

    fun addPose(pose: Pose2d) {
        routines.add(BlankMotionRoutine(
            requireNotNull(endPose) { "Continuation of autonomous builder after split conditional trajectory" }
                    + pose
        ))
    }

    fun task(block: suspend CoroutineScope.() -> Unit) {
        routines.add(block)
    }

    fun asyncTask(block: suspend CoroutineScope.() -> Unit) = AsyncTask(block).also {
        routines.add(it)
    }

    fun awaitTask(task: AsyncTask) {
        routines.add {
            task.job.join()
        }
    }

    @JvmName("waitMillis")
    fun wait(millis: Long) {
        routines.add {
            delay(millis)
        }
    }

    fun asyncScope(block: AutonomousBuilder.() -> Unit) {
        routines.add(createSubroutine().apply(block))
    }

    fun loopTrajectory(block: LoopTrajectoryBuilder.() -> Unit) {
        routines.add(LoopTrajectoryBuilder(createSubroutine()).apply(block).motionRoutine)
    }

    fun conditionalTrajectory(block: ConditionalTrajectoryBuilder.() -> Unit) {
        routines.add(ConditionalTrajectoryBuilder(::createSubroutine).apply(block).motionRoutine)
    }

    override suspend fun CoroutineScope.runTask() = coroutineScope {
        routines.forEach {
            if (it is AsyncTask) with(it) { launchTask() }
            else coroutineScope { with(it) { runTask() } }
        }
    }

    fun run() = runBlocking {
        runTask()
    }
}