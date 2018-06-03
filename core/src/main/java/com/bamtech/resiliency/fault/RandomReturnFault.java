// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.fault;

import java.lang.reflect.Method;

import scala.Option;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Return;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.scalacheck.Gen;

import com.bamtech.resiliency.annotation.RandomReturnValue;
import com.bamtech.resiliency.util.EvaluateGenExpr;

/**
 * Transforms class byte code by using a Scalacheck generator to return values whenever a `RandomReturnValue`
 * annotated method is called.
 */
public class RandomReturnFault implements Transformer {

  @Override
  public DynamicType.Builder<?> transform(
    DynamicType.Builder<?> builder,
    TypeDescription typeDescription,
    ClassLoader classLoader,
    JavaModule module
  ) {
    return
      builder
        .method(ElementMatchers.isAnnotatedWith(RandomReturnValue.class))
        .intercept(Advice.to(RandomReturnFaultImpl.class));
  }
}

class RandomReturnFaultImpl {

  @OnMethodExit
  static public void exit(
    @Origin Method method,
    @Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object value
  ) {
    if (value != null) {
      Class<?> returnType = method.getReturnType();
      String genExpr = method.getDeclaredAnnotation(RandomReturnValue.class).generator();
      Gen<Option<Object>> gen = EvaluateGenExpr.<Object>apply(genExpr);
      Option<Option<Object>> result = gen.sample();

      if (result.isDefined() && result.get().isDefined() && returnType.isInstance(result.get().get())) {
        value = result.get().get();
      }
    }
  }
}
