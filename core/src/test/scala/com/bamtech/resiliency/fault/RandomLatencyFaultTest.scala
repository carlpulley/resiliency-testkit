// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.fault

import scala.concurrent.duration._
import scala.util.{Failure, Try}

import net.bytebuddy.agent.ByteBuddyAgent
import net.bytebuddy.agent.builder.ResettableClassFileTransformer
import org.scalacheck.Gen
import org.scalatest.concurrent.TimeLimits
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.scalatest._

import com.bamtech.resiliency.ResiliencyFaultAgent
import com.bamtech.resiliency.annotation.RandomLatency
import com.bamtech.resiliency.util.ValueDiscard

class RandomLatencyFaultTest
  extends FreeSpec
    with Matchers
    with BeforeAndAfterAll
    with TimeLimits
    with Inspectors
    with Inside {

  import RandomLatencyFaultTest._

  override def beforeAll(): Unit = {
    ByteBuddyAgent.install()
    ValueDiscard[ResettableClassFileTransformer] {
      ResiliencyFaultAgent.agentInstrumentation("com.bamtech.resiliency.fault.*").installOnByteBuddyAgent()
    }
  }

  "RandomLatency" in {
    val results = (0 until sampleSize).map { _ =>
      Try {
        failAfter(sleepPeriod - 10.milliseconds) {
          testMethod()
        }
      }
    }

    results.count(_.isSuccess) should be > 0
    results.count(_.isFailure) should be > 0
    forAll(results.filter(_.isFailure)) { failure =>
      inside(failure) {
        case Failure(cause) =>
          cause shouldBe an[TestFailedDueToTimeoutException]
      }
    }
    results.count(_.isSuccess) + results.count(_.isFailure) shouldEqual sampleSize
  }
}

object RandomLatencyFaultTest {
  val sampleSize: Int = 200
  val sleepPeriod: FiniteDuration = 200.milliseconds
  val latencyGen: Gen[Option[FiniteDuration]] = {
    Gen.option(Gen.const(sleepPeriod))
  }

  @RandomLatency(generator = "com.bamtech.resiliency.fault.RandomLatencyFaultTest.latencyGen")
  def testMethod(): String = {
    "Success"
  }
}
