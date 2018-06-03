// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.fault

import net.bytebuddy.agent.ByteBuddyAgent
import net.bytebuddy.agent.builder.ResettableClassFileTransformer
import org.scalacheck.Gen
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Inspectors, Matchers}

import com.bamtech.resiliency.ResiliencyFaultAgent
import com.bamtech.resiliency.annotation.RandomReturnValue
import com.bamtech.resiliency.util.ValueDiscard

class RandomReturnFaultTest extends FreeSpec with Matchers with BeforeAndAfterAll with Inspectors {

  import RandomReturnFaultTest._

  override def beforeAll(): Unit = {
    ByteBuddyAgent.install()
    ValueDiscard[ResettableClassFileTransformer] {
      ResiliencyFaultAgent.agentInstrumentation("com.bamtech.resiliency.fault.*").installOnByteBuddyAgent()
    }}

  "RandomReturnValue" in {
    val results = (0 until sampleSize).map { _ =>
      testMethod()
    }

    results.count(_ == "Success") shouldEqual ((sampleSize * noneFreq) / totalOptionFreq) +- errorDelta
    forAll(results.filter(_ != "Success")) { value =>
      value should fullyMatch regex "[0-9]*"
    }
    results.count(_ != "Success") shouldEqual ((sampleSize * someFreq) / totalOptionFreq) +- errorDelta
  }
}

object RandomReturnFaultTest {
  val sampleSize: Int = 100
  val errorDelta: Int = 6
  val noneFreq: Int = 1
  val someFreq: Int = 9
  val totalOptionFreq: Int = noneFreq + someFreq

  val valueGen: Gen[Option[String]] = {
    Gen.option(Gen.numStr)
  }

  @RandomReturnValue(generator = "com.bamtech.resiliency.fault.RandomReturnFaultTest.valueGen")
  def testMethod(): String = {
    "Success"
  }
}
