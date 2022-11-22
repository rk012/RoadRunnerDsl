package com.outoftheboxrobotics.roadrunnerdsl

import com.outoftheboxrobotics.roadrunnerdsl.AutonomousBuilderTest.Companion.autonomousBuilder
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LoopTrajectoryTest {
    @Test
    fun loopTest() {
        val numLoops = 10
        var remaining = numLoops + 1
        var counter = 0

        autonomousBuilder {
            loopTrajectory {
                condition {
                    remaining--
                    remaining > 0
                }

                body {
                    task { counter++ }
                }
            }
        }.run()

        assertEquals(numLoops, counter)
    }

    @Test
    fun loopExceptionTest() {
        assertThrows<IllegalArgumentException> {
            autonomousBuilder {
                loopTrajectory {  }
            }
        }

        autonomousBuilder {
            loopTrajectory {
                condition { false }
            }
        }

        assertThrows<IllegalArgumentException> {
            autonomousBuilder {
                loopTrajectory {
                    condition { false }
                    body {
                        trajectory {
                            strafeRight(10.0)
                        }
                    }
                }
            }
        }

        autonomousBuilder {
            loopTrajectory {
                condition { false }
                body {
                    trajectory { forward(10.0) }
                    trajectory { back(15.0) }
                    trajectory { forward(5.0) }
                }
            }
        }
    }
}