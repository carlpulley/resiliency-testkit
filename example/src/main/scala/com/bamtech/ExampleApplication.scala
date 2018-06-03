// Copyright 2018 Carl Pulley

package com.bamtech

import com.bamtech.resiliency.annotation.RandomException

/**
  * Example application demonstrating how the resiliency fault gaent may be used.
  *
  * To run this application with the faulting agent instrumenting your code use:
  * <pre>
  * {@code
  * sbt compile core/assembly
  * java -javaagent:./core/target/scala-2.12/resiliency-fault-agent-1.0.0.jar="com.bamtech.*" -jar ./example/target/scala-2.12/resiliency-example_2.12.jar
  * }
  * </pre>
  */
object ExampleApplication extends App {

  println(getMessage())

  /**
    * Simple message generator method.
    *
    * @return message
    */
  @RandomException(
    generator =
      """
        import org.scalacheck.Gen

        Gen.frequency(
          1 -> Gen.const(None),
          1 -> Gen.const(Some(new Exception("FakeRandomException")))
        )
      """
  )
  def getMessage(): String = {
    "Success"
  }
}
