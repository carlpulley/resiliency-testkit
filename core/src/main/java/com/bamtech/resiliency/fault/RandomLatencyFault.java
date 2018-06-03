// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.fault;

import java.lang.reflect.Method;

import scala.Option;
import scala.concurrent.duration.FiniteDuration;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.scalacheck.Gen;

import com.bamtech.resiliency.annotation.RandomLatency;
import com.bamtech.resiliency.util.EvaluateGenExpr;

/**
 * Transforms class byte code by using a Scalacheck generator to delay method calls whenever a `RandomLatency`
 * annotated method or constructor is called.
 */
public class RandomLatencyFault implements AgentBuilder.Transformer {

  @Override
  public DynamicType.Builder<?> transform(
    DynamicType.Builder<?> builder,
    TypeDescription typeDescription,
    ClassLoader classLoader,
    JavaModule module
  ) {
    return
      builder
        .method(ElementMatchers.isAnnotatedWith(RandomLatency.class))
        .intercept(Advice.to(RandomLatencyImpl.class));
  }
}

class RandomLatencyImpl {

  @OnMethodEnter
  static public void enter(@Origin Method method) throws InterruptedException {
    String genExpr = method.getDeclaredAnnotation(RandomLatency.class).generator();
    Gen<Option<FiniteDuration>> gen = EvaluateGenExpr.<FiniteDuration>apply(genExpr);
    Option<Option<FiniteDuration>> result = gen.sample();

    if (result.isDefined() && result.get().isDefined()) {
        Thread.sleep(result.get().get().toMillis());
    }
  }
}
