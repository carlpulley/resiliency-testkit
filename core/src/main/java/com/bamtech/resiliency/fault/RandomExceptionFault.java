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

import com.bamtech.resiliency.annotation.RandomException;
import com.bamtech.resiliency.util.EvaluateGenExpr;

/**
  * Transforms class byte code by using a Scalacheck generator to throw random exceptions whenever a `RandomException`
  * annotated method or constructor is called.
  */
public class RandomExceptionFault implements Transformer {

  @Override
  public DynamicType.Builder<?> transform(
    DynamicType.Builder<?> builder,
    TypeDescription typeDescription,
    ClassLoader classLoader,
    JavaModule module
  ) {
    return
      builder
        .method(ElementMatchers.isAnnotatedWith(RandomException.class))
        .intercept(Advice.to(RandomExceptionImpl.class));
  }
}

class RandomExceptionImpl {

  @OnMethodEnter
  static public void enter(@Origin Method method) throws Throwable {
    String genExpr = method.getDeclaredAnnotation(RandomException.class).generator();
    Gen<Option<Throwable>> gen = EvaluateGenExpr.<Throwable>apply(genExpr);
    Option<Option<Throwable>> result = gen.sample();

    if (result.isDefined() && result.get().isDefined()) {
      throw result.get().get();
    }
  }
}
