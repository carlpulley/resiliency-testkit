// Copyright 2018 Carl Pulley

package com.bamtech.resiliency

import java.io.File
import java.lang.instrument.Instrumentation

import net.bytebuddy.agent.ByteBuddyAgent
import net.bytebuddy.agent.builder.{AgentBuilder, ResettableClassFileTransformer}
import net.bytebuddy.description.`type`.TypeDescription
import net.bytebuddy.matcher.ElementMatchers

import com.bamtech.resiliency.fault.{RandomExceptionFault, RandomLatencyFault, RandomParameterFault, RandomReturnFault}
import com.bamtech.resiliency.util.ValueDiscard

/**
  * Agent for injecting random faults (for resiliency testing) into JVM code.
  */
object ResiliencyFaultAgent {

  // $COVERAGE-OFF$

  /**
    * Attach the given agent to the supplied process ID.
    *
    * @param args whitelist regular expression of packages to instrument and a process ID
    */
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println(s"Usage: resiliency-fault-agent.jar PACKAGE_REGEX PROCESS_ID")
    } else {
      val agentJar = new File(this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath)
      val processId = args(1).toInt
      val regex = args(0)

      assert(agentJar.getName.startsWith("resiliency-fault-agent") && agentJar.getName.endsWith(".jar"))
      assert(agentJar.exists() && agentJar.canRead)

      ByteBuddyAgent.attach(agentJar, processId.toString, regex)
    }
  }

  /**
    * If the agent is attached to a JVM on the start, then this method is invoked before {@code main} method is called.
    *
    * @param regex a regular expression argument for matching instrumented methods.
    * @param instrumentation An object to access the JVM instrumentation mechanism.
    */
  def premain(regex: String, instrumentation: Instrumentation): Unit = {
    ValueDiscard[ResettableClassFileTransformer] {
      agentInstrumentation(regex).installOn(instrumentation)
    }
  }

  /**
    * If the agent is attached to an already running JVM, then this method is invoked.
    *
    * @param regex a regular expression argument for matching instrumented methods.
    * @param instrumentation An object to access the JVM instrumentation mechanism.
    */
  def agentmain(regex: String, instrumentation: Instrumentation): Unit = {
    ValueDiscard[ResettableClassFileTransformer] {
      agentInstrumentation(regex).installOn(instrumentation)
    }
  }

  // $COVERAGE-ON$

  private[resiliency] def agentInstrumentation(regex: String): AgentBuilder = {
    import ElementMatchers._

    new AgentBuilder.Default()
//      .`with`(AgentBuilder.Listener.StreamWriting.toSystemOut)
      .`with`(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//      .`with`(AgentBuilder.TypeStrategy.Default.REDEFINE)
//      .disableClassFormatChanges()
      .`type`(nameMatches[TypeDescription](regex))
      .transform(new RandomExceptionFault)
      .asDecorator()
      .`type`(nameMatches[TypeDescription](regex))
      .transform(new RandomLatencyFault)
      .asDecorator()
      .`type`(nameMatches[TypeDescription](regex))
      .transform(new RandomParameterFault)
      .asDecorator()
      .`type`(nameMatches[TypeDescription](regex))
      .transform(new RandomReturnFault)
      .asDecorator()
  }
}
