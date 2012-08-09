import sbt._
import sbt.Keys._

object ProjectBuild extends Build {

  lazy val buildVersion =  "1.0.7-HOTFIX-SNAPSHOT"

  lazy val root = Project(id = "play-plugins-salat", base = file("."), settings = Project.defaultSettings).settings(
    organization := "se.radley",
    description := "MongoDB Salat plugin for PlayFramework 2",
    version := buildVersion,
    scalaVersion := "2.9.1",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    libraryDependencies += "play" %% "play" % "2.0.3",
    libraryDependencies += "play" %% "play-test" % "2.0.3" % "test",
    libraryDependencies += "com.novus" %% "salat" % "1.9.0",
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://github.com/leon/play-salat</url>
      <licenses>
        <license>
          <name>Apache 2.0</name>
          <url>http://www.opensource.org/licenses/Apache-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:wishbear/play-salat.git</url>
        <connection>scm:git:git@github.com:wishbear/play-salat.git</connection>
      </scm>
      <developers>
        <developer>
          <id>leon</id>
          <name>Leon Radley</name>
          <url>http://leon.radley.se</url>
        </developer>
      </developers>
    ),
    publishTo <<= version { version: String =>
      val artifactory = "http://repo.myhotspot.ru/"
      if (version.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at artifactory + "snapshots")
      else
        Some("releases" at artifactory + "releases")
    }
  )
}
