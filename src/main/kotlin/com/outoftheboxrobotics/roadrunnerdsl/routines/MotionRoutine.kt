package com.outoftheboxrobotics.roadrunnerdsl.routines

import com.acmerobotics.roadrunner.geometry.Pose2d

internal interface MotionRoutine : AutonomousRoutine {
    val endPose: Pose2d
}