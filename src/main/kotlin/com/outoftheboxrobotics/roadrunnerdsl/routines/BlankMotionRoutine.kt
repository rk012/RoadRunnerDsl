package com.outoftheboxrobotics.roadrunnerdsl.routines

import com.acmerobotics.roadrunner.geometry.Pose2d

@JvmInline
internal value class BlankMotionRoutine(override val endPose: Pose2d) : MotionRoutine {
    // Do nothing
    override suspend fun runTask() {}
}