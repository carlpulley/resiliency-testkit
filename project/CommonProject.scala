// Copyright 2016-2018 Carl Pulley

import Dependencies.Scala
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.{coverageExcludedFiles, coverageFailOnMinimum, coverageMinimum}

/**
  * Common project settings
  */
object CommonProject {
  val settings =
    Seq(
      organization := "com.bamtech",
      scalaVersion := Scala.version,
      scalacOptions in Compile ++= Seq(
        "-deprecation",
        "-encoding", "utf-8",
        "-explaintypes",
        "-feature",
        "-language:existentials",
        "-language:experimental.macros",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-unchecked",
        "-Xcheckinit",
        "-Xfatal-warnings",
        "-Xfuture",
        "-Xlint:adapted-args",
        "-Xlint:by-name-right-associative",
        "-Xlint:constant",
        "-Xlint:delayedinit-select",
        "-Xlint:doc-detached",
        "-Xlint:inaccessible",
        "-Xlint:infer-any",
        "-Xlint:missing-interpolator",
        "-Xlint:nullary-override",
        "-Xlint:nullary-unit",
        "-Xlint:option-implicit",
        "-Xlint:package-object-classes",
        "-Xlint:poly-implicit-overload",
        "-Xlint:private-shadow",
        "-Xlint:stars-align",
        "-Xlint:type-parameter-shadow",
        "-Xlint:unsound-match",
        "-Yno-adapted-args",
        "-Ypartial-unification",
        "-Ywarn-dead-code",
        "-Ywarn-extra-implicit",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit",
        "-Ywarn-numeric-widen",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates",
        "-Ywarn-value-discard"
      ),
      scalacOptions in (Compile, doc) ++= {
        val nm = (name in(Compile, doc)).value
        val ver = (version in(Compile, doc)).value

        DefaultOptions.scaladoc(nm, ver)
      },
      javacOptions in (Compile, compile) ++= Seq(
        "-source", "1.8",
        "-target", "1.8",
        "-Xlint:all",
        "-Xlint:-options",
        "-Xlint:-path",
        "-Xlint:-processing",
        "-Werror"
      ),
      javacOptions in doc := Seq(),
      javaOptions ++= Seq("-Xmx2G"),
      outputStrategy := Some(StdoutOutput),
      testOptions in Test += Tests.Argument("-oFD"),
      fork := true,
      fork in Test := true,
      coverageMinimum := 0,
      coverageFailOnMinimum := true,
      coverageExcludedFiles := ".*/target/.*"
    )
}
