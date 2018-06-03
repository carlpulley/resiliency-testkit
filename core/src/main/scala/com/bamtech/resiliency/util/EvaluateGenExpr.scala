// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.util

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

import org.scalacheck.Gen

/**
  * TODO:
  */
object EvaluateGenExpr {

  /**
    * TODO:
    *
    * @param expr
    * @tparam T
    * @return
    */
  def apply[T](expr: String): Gen[Option[T]] = {
    val toolbox = currentMirror.mkToolBox()
    val tree = toolbox.parse(expr)

    toolbox.eval(tree).asInstanceOf[Gen[Option[T]]]
  }

  /**
    * TODO:
    *
    * @param expr
    * @param baseType
    * @return
    */
  def apply(expr: String, baseType: Class[_]): Gen[Option[_]] = {
    val toolbox = currentMirror.mkToolBox()
    val tree = toolbox.parse(expr)
    val gen = toolbox.eval(tree).asInstanceOf[Gen[Option[Object]]]

    gen.map(_.map(baseType.cast))
  }
}
