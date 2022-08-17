package com.outoftheboxrobotics.roadrunnerdsl

import com.outoftheboxrobotics.roadrunnerdsl.AutonomousBuilderTest.Companion.autonomousBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AsyncTest {
    @Test
    fun asyncTest() {
        var s = ""

        autonomousBuilder {
            task { s += "1" }

            asyncTask {
                coroutineScope {
                    delay(20L)
                    s += "3"
                }
            }

            asyncScope {
                asyncTask {
                    coroutineScope {
                        delay(50L)
                        s += "4"
                    }
                }

                task { s += "2" }
            }

            task { s += "5" }
        }.run()

        assertEquals("12345", s)
    }
}