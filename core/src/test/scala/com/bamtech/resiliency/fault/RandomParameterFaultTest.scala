// Copyright 2018 Carl Pulley
/*
package com.bamtech.resiliency.fault

import java.util.concurrent.atomic.AtomicInteger

import net.bytebuddy.agent.ByteBuddyAgent
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

import com.bamtech.resiliency.ResiliencyFaultAgent
import com.bamtech.resiliency.annotation.RandomParameterValue
import com.bamtech.resiliency.util.ValueDiscard

class RandomParameterFaultTest
  extends FreeSpec
    with Matchers
    with BeforeAndAfterAll
    with GeneratorDrivenPropertyChecks {

  import RandomParameterFaultTest._

  override def beforeAll(): Unit = {
    ByteBuddyAgent.install()
    ValueDiscard[ResettableClassFileTransformer] {
      ResiliencyFaultAgent.agentInstrumentation("com.bamtech.resiliency.fault.*").installOnByteBuddyAgent()
    }
  }

  "RandomParameterValue" in {
    val sampleSize: AtomicInteger = new AtomicInteger(0)
    val successCount: AtomicInteger = new AtomicInteger(0)

    forAll(Gen.alphaLowerStr, Gen.alphaLowerStr, Gen.alphaLowerStr) {
      case (param1, param2, param3) =>
        val result = testMethod(param1, param2, param3)
        result should startWith(param1)
        result should endWith(param3)
        ValueDiscard[Int] {
          sampleSize.incrementAndGet()
        }
        if (result == param1 + param2 + param3) {
          ValueDiscard[Int] {
            successCount.incrementAndGet()
          }
        }
    }

    successCount.get() shouldEqual ((sampleSize.get() * someFreq) / totalOptionFreq) +- errorDelta
  }
}

object RandomParameterFaultTest {
  val errorDelta: Int = 5
  val noneFreq: Int = 1
  val someFreq: Int = 9
  val totalOptionFreq: Int = noneFreq + someFreq

  val valueGen: Gen[Option[String]] = {
    Gen.option(Gen.numStr)
  }

  def testMethod(
    param1: String,
    @RandomParameterValue(generator = "com.bamtech.resiliency.fault.RandomParameterFaultTest.valueGen")
    param2: String,
    param3: String
  ): String = {
    param1 + param2 + param3
  }
}
*/
