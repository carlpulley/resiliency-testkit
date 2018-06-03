// Copyright 2016-2018 Carl Pulley

package com.bamtech.resiliency.util

/**
  * Function that allows values to be discarded in a type visible way.
  */
object ValueDiscard {

  /**
    * Function that allows values to be discarded in a type visible way.
    *
    * @tparam T type of the value that will be computed (it won't be inferred,
    *   must be specified)
    * @return function accepting value expression that needs to be computed and
    *   whose value will be discarded
    */
  def apply[T]: (=> T) => Unit = { value =>
    val _ = value
  }
}
