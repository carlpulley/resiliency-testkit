// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.fault

import scala.util.{Failure, Try}

import net.bytebuddy.agent.ByteBuddyAgent
import net.bytebuddy.agent.builder.ResettableClassFileTransformer
import org.scalacheck.Gen
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

import com.bamtech.resiliency.ResiliencyFaultAgent
import com.bamtech.resiliency.annotation.RandomException
import com.bamtech.resiliency.util.ValueDiscard

class RandomExceptionFaultTest extends FreeSpec with Matchers with BeforeAndAfterAll {

  import RandomExceptionFaultTest._

  override def beforeAll(): Unit = {
    ByteBuddyAgent.install()
    ValueDiscard[ResettableClassFileTransformer] {
      ResiliencyFaultAgent.agentInstrumentation("com.bamtech.resiliency.fault.*").installOnByteBuddyAgent()
    }
  }

  "RandomException" in {
    val results = (0 until sampleSize).map { _ =>
      Try(testMethod())
    }

    results.count(_.isSuccess) should be > 0
    results.count(_ == Failure(FakeTestException)) should be > 0
    results.count(_ == Failure(FakeTestError)) should be > 0
    results.count(_ == Failure(FakeTestThrowable)) should be > 0
    results.count(_.isSuccess) +
      results.count(_ == Failure(FakeTestException)) +
      results.count(_ == Failure(FakeTestError)) +
      results.count(_ == Failure(FakeTestThrowable)) shouldEqual sampleSize
  }
}

object RandomExceptionFaultTest {
  val sampleSize: Int = 200
  val exceptionFreq: Int = 3
  val errorFreq: Int = 1
  val throwableFreq: Int = 2

  case object FakeTestException extends Exception("FakeTestException")

  case object FakeTestError extends Error("FakeTestError")

  case object FakeTestThrowable extends Throwable("FakeTestThrowable")

  val errorGen: Gen[Option[Throwable]] = {
    Gen.option(Gen.frequency(
      exceptionFreq -> FakeTestException,
      errorFreq -> FakeTestError,
      throwableFreq -> FakeTestThrowable
    ))
  }

  @RandomException(generator = "com.bamtech.resiliency.fault.RandomExceptionFaultTest.errorGen")
  def testMethod(): String = {
    "Success"
  }
}
