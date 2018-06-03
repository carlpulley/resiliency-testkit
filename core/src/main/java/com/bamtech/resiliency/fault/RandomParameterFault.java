// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.fault;

import java.lang.reflect.Method;

import scala.Option;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.scalacheck.Gen;

import com.bamtech.resiliency.annotation.RandomParameterValue;
import com.bamtech.resiliency.util.EvaluateGenExpr;

/**
 * TODO:
 */
public class RandomParameterFault implements Transformer {

  @Override
  public DynamicType.Builder<?> transform(
    DynamicType.Builder<?> builder,
    TypeDescription typeDescription,
    ClassLoader classLoader,
    JavaModule module
  ) {
    return
      builder
        .method(ElementMatchers.isAnnotatedWith(RandomParameterValue.class))
        .intercept(Advice.to(RandomParameterValueImpl.class));
  }
}

class RandomParameterValueImpl {

  @OnMethodEnter
  static public void enter(@Origin Method method) {
    String genExpr = method.getDeclaredAnnotation(RandomParameterValue.class).generator();
    Gen<Option<Throwable>> gen = EvaluateGenExpr.<Throwable>apply(genExpr);
    Option<Option<Throwable>> result = gen.sample();

    if (result.isDefined() && result.get().isDefined()) {
      // FIXME:
      result.get().get();
    }
  }
}
