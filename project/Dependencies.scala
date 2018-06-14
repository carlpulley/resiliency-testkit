// Copyright 2016-2018 Carl Pulley

import sbt._

object Dependencies {

  object ByteBuddy {
    val version = "1.8.12"

    val agent:ModuleID = "net.bytebuddy" % "byte-buddy-agent" % version
    val core: ModuleID = "net.bytebuddy" % "byte-buddy" % version
  }

  object FindBugs {
    val version = "3.0.1"

    val annotations: ModuleID = "com.google.code.findbugs" % "findbugs-annotations" % version
  }

  object Scala {
    val version = "2.12.6"

    val compiler: ModuleID = "org.scala-lang" % "scala-compiler" % version
  }

  val fastpath: ModuleID = "io.github.lukehutch" % "fast-classpath-scanner" % "2.21"
  val scalacheck: ModuleID = "org.scalacheck" %% "scalacheck" % "1.14.0"
  val scalalogging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
  val scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
}
