// Copyright 2016-2018 Carl Pulley

import java.nio.file.Files

import Dependencies._

//enablePlugins(GhpagesPlugin)
enablePlugins(GitBranchPrompt)
enablePlugins(GitVersioning)
enablePlugins(SiteScaladocPlugin)

Publish.settings

git.useGitDescribe := true
ivyLoggingLevel := UpdateLogging.Quiet

lazy val annotation = project.in(file("annotation"))
  .settings(CommonProject.settings)
  .settings(ScalaDoc.settings)
  .settings(
    name := "resiliency-annotation",
    coverageMinimum := 0
  )

lazy val core = project.in(file("core"))
  .dependsOn(annotation)
  .settings(CommonProject.settings)
  .settings(ScalaDoc.settings)
  .settings(
    name := "resiliency-core",
    libraryDependencies ++= Seq(
      ByteBuddy.agent,
      ByteBuddy.core,
      FindBugs.annotations,
      Scala.compiler,
      scalacheck,
      scalalogging,
      scalatest % Test
    ),
    assemblyJarName in assembly := s"resiliency-fault-agent-${version.value}.jar",
    packageOptions in (Compile, packageBin) += {
      import java.util.jar.{Attributes, Manifest}

      val manifest = new Manifest

      manifest.getMainAttributes.put(
        new Attributes.Name("Main-Class"),
        "com.bamtech.resiliency.ResiliencyFaultAgent"
      )
      manifest.getMainAttributes.put(
        new Attributes.Name("Premain-Class"),
        "com.bamtech.resiliency.ResiliencyFaultAgent"
      )
      manifest.getMainAttributes.put(
        new Attributes.Name("Agent-Class"),
        "com.bamtech.resiliency.ResiliencyFaultAgent"
      )
      manifest.getMainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true")
      manifest.getMainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true")

      Package.JarManifest(manifest)
    },
    artifact in (Compile, assembly) := {
      val art = (artifact in (Compile, assembly)).value
      art.copy(`classifier` = Some("assembly"))
    },
    addArtifact(artifact in (Compile, assembly), assembly),
    test in assembly := {}, // FIXME:
    coverageMinimum := 100
  )

lazy val faultAgent = project
  .settings(CommonProject.settings)
  .settings(
    name := "resiliency-fault-agent",
    packageBin in Compile := (assembly in (core, Compile)).value
  )

lazy val testkit = project.in(file("testkit"))
  .dependsOn(core)
  .settings(CommonProject.settings)
  .settings(ScalaDoc.settings)
  .settings(
    name := "resiliency-testkit",
    libraryDependencies ++= Seq(
      fastpath,
      scalatest,
      scalacheck
    ),
    coverageMinimum := 0
  )

lazy val example = project.in(file("example"))
  .dependsOn(annotation, testkit % "compile->test")
  .enablePlugins(JavaAppPackaging)
  .settings(CommonProject.settings)
  .settings(
    name := "resiliency-example",
    mainClass in Compile := Some("com.bamtech.ExampleApplication"),
    dockerBaseImage := "openjdk:8-jre-slim",
    version in Docker := version.value,
    dockerUpdateLatest := true,
    coverageMinimum := 0,
    publishLocal := {},
    publish := {},
    publishLocal in Docker := {
      (assembly in core).value
      val srcAgentJar = s"${(name in faultAgent).value}-${version.value}.jar"
      val destAgentJar = new File(s"example/docker/agent/${(name in faultAgent).value}.jar")
      if (destAgentJar.exists()) {
        Files.delete(destAgentJar.toPath)
      }
      Files.copy(
        new File(s"${(target in core).value}/scala-${scalaBinaryVersion.value}/$srcAgentJar").toPath,
        destAgentJar.toPath
      )
      (publishLocal in Docker).value
    },
    publish in Docker := {
      (assembly in core).value
      val srcAgentJar = s"${(name in faultAgent).value}-${version.value}.jar"
      val destAgentJar = new File(s"example/docker/agent/${(name in faultAgent).value}.jar")
      if (destAgentJar.exists()) {
        Files.delete(destAgentJar.toPath)
      }
      Files.copy(
        new File(s"${(target in core).value}/scala-${scalaBinaryVersion.value}/$srcAgentJar").toPath,
        destAgentJar.toPath
      )
      (publish in Docker).value
    },
    clean := {
      val agentJar = new File(s"example/docker/agent/${(name in faultAgent).value}.jar")
      if (agentJar.exists()) {
        Files.delete(agentJar.toPath)
      }
      clean.value
    }
  )
