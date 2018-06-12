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

  "RandomReturnValue" - {
    "with data return type" in {
      val results = (0 until sampleSize).map { _ =>
        testDataMethod()
      }

      results.count(_ == "Success") should be > 0
      results.count(_ != "Success") should be > 0
      forAll(results.filter(_ != "Success")) { value =>
        value should fullyMatch regex "[0-9]*"
      }
      results.count(_ == "Success") + results.count(_ != "Success") shouldEqual sampleSize
    }

    "with function return type" in {
      val results = (0 until sampleSize).map { _ =>
        testFunctionMethod()(())
      }

      results.count(_ == "Success") should be > 0
      results.count(_ != "Success") should be > 0
      forAll(results.filter(_ != "Success")) { value =>
        value should fullyMatch regex "[0-9]*"
      }
      results.count(_ == "Success") + results.count(_ != "Success") shouldEqual sampleSize
    }
  }
}

object RandomReturnFaultTest {
  val sampleSize: Int = 200
  val valueGen: Gen[Option[String]] = {
    Gen.option(Gen.numStr)
  }
  val functionGen: Gen[Option[Unit => String]] = {
    Gen.option(Gen.numStr.map(num => _ => num))
  }

  @RandomReturnValue(generator = "com.bamtech.resiliency.fault.RandomReturnFaultTest.valueGen")
  def testDataMethod(): String = {
    "Success"
  }

  @RandomReturnValue(generator = "com.bamtech.resiliency.fault.RandomReturnFaultTest.functionGen")
  def testFunctionMethod(): Unit => String = {
    _ => "Success"
  }
}
