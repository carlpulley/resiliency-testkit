// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.util

import org.scalacheck.Gen
import org.scalatest.{FreeSpec, Matchers}

class EvaluateGenExprTest extends FreeSpec with Matchers {

  import EvaluateGenExprTest._

  "EvaluateGenExpr" - {
    "using val reference" in {
      val generator: Gen[Option[Int]] = EvaluateGenExpr[Int]("com.bamtech.resiliency.util.EvaluateGenExprTest.valRef")
      val results = (0 until sampleSize).map(_ => generator.sample).collect {
        case Some(value) => value
      }

      results.count(_.isDefined) shouldEqual (sampleSize * someFreq / totalFreq) +- errorDelta
      results.count(_.exists(_ > 0)) shouldEqual (sampleSize * someFreq / totalFreq) +- errorDelta
    }

    "using simple Scala expression" in {
      val generator: Gen[Option[String]] = EvaluateGenExpr[String](simpleExpr)
      val results = (0 until sampleSize).map(_ => generator.sample).collect {
        case Some(value) => value
      }

      results.count(_.isDefined) shouldEqual (sampleSize * someFreq / totalFreq) +- errorDelta
      results.count(_.exists(_.matches("[a-zA-Z0-9]*"))) shouldEqual (sampleSize * someFreq / totalFreq) +- errorDelta
    }

    "using complex Scala expression" in {
      val generator: Gen[Option[String]] = EvaluateGenExpr[String](complexExpr)
      val results = (0 until sampleSize).map(_ => generator.sample).collect {
        case Some(value) => value
      }

      results.count(_.isDefined) shouldEqual (sampleSize * someFreq / totalFreq) +- errorDelta
      results.count(_.exists(_.matches("[a-zA-Z0-9]*"))) shouldEqual (sampleSize * someFreq / totalFreq) +- errorDelta
    }
  }
}

object EvaluateGenExprTest {
  val sampleSize: Int = 1000000
  val errorDelta: Int = 1000
  val noneFreq: Int = 1
  val someFreq: Int = 9
  val totalFreq: Int = noneFreq + someFreq

  val valRef: Gen[Option[Int]] = {
    Gen.option(Gen.posNum[Int])
  }

  val simpleExpr: String = "org.scalacheck.Gen.option(org.scalacheck.Gen.alphaNumStr)"

  val complexExpr: String =
    """
      |import org.scalacheck.Gen
      |
      |Gen.option(Gen.alphaNumStr)
    """.stripMargin
}
