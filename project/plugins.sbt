// Copyright 2016-2018 Carl Pulley

scalacOptions ++= Seq("-deprecation", "-Xlint", "-unchecked", "-language:_", "-Xfatal-warnings")

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

val xml: ModuleID = "xml-apis" % "xml-apis" % "1.3.04"

dependencyOverrides ++= Set(xml)

//addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.11")
addSbtPlugin("com.dwijnand" % "sbt-travisci" % "1.1.1")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.1")
addSbtPlugin("com.updateimpact" % "updateimpact-sbt-plugin" % "2.1.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
