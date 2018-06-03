// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.testkit

import java.lang.reflect.Executable

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor
import org.scalatest.{FreeSpec, Matchers}

import com.bamtech.resiliency.annotation.{RandomException, RandomLatency /*, RandomParameterValue*/, RandomReturnValue}
import com.bamtech.resiliency.util.EvaluateGenExpr

/**
  * Resiliency agent testkit.
  *
  * Facilitates testing of resiliency annotated methods and constructors. Allows annotation generators to be validated
  * prior to runtime.
  */
abstract class ResiliencyTestkit extends FreeSpec with Matchers {

  "Resiliency fault agent" - {
    "RandomException" in {
      val generators = mutable.HashSet.empty[String]
      val scanner = new FastClasspathScanner

      scanner.matchClassesWithMethodAnnotation(classOf[RandomException], new MethodAnnotationMatchProcessor {
        override def processMatch(matchingClass: Class[_], matchingMethodOrConstructor: Executable): Unit = {
          generators += matchingMethodOrConstructor.getAnnotation(classOf[RandomException]).generator()
        }
      })
      scanner.scan()

      val results = generators.map(gen => Try(EvaluateGenExpr[Throwable](gen)))

      results.count(_.isFailure) shouldEqual 0
    }

    "RandomLatency" in {
      val generators = mutable.HashSet.empty[String]
      val scanner = new FastClasspathScanner

      scanner.matchClassesWithMethodAnnotation(classOf[RandomLatency], new MethodAnnotationMatchProcessor {
        override def processMatch(matchingClass: Class[_], matchingMethodOrConstructor: Executable): Unit = {
          generators += matchingMethodOrConstructor.getAnnotation(classOf[RandomLatency]).generator()
        }
      })
      scanner.scan()

      val results = generators.map(gen => Try(EvaluateGenExpr[FiniteDuration](gen)))

      results.count(_.isFailure) shouldEqual 0
    }

//    "RandomParameterValue" in {
//      val generators = annotationGenerators[RandomParameterValue]
//      val results = generators.map(gen => Try(EvaluateGenExpr[_](gen)))
//
//      results.count(_.isFailure) shouldEqual 0
//    }

    "RandomReturnValue" in {
      val generators = mutable.HashMap.empty[Class[_], String]
      val scanner = new FastClasspathScanner

      scanner.matchClassesWithMethodAnnotation(classOf[RandomReturnValue], new MethodAnnotationMatchProcessor {
        override def processMatch(matchingClass: Class[_], matchingMethodOrConstructor: Executable): Unit = {
          val returnType =
            matchingClass
              .getDeclaredMethod(
                matchingMethodOrConstructor.getName,
                matchingMethodOrConstructor.getParameterTypes: _*
              )
              .getReturnType
          generators += returnType -> matchingMethodOrConstructor.getAnnotation(classOf[RandomReturnValue]).generator()
        }
      })
      scanner.scan()

      val results = generators.map {
        case (returnType, genExpr) =>
          Try(EvaluateGenExpr(genExpr, returnType))
      }

      results.count(_.isFailure) shouldEqual 0
    }
  }
}
