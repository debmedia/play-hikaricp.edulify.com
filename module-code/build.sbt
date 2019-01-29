import play.Project._

name := "play-hirakicp"

version := "1.2.2"

libraryDependencies ++= Seq(
  jdbc,
  "com.zaxxer" % "HikariCP" % "3.3.0",
  "commons-configuration" % "commons-configuration" % "1.10",
  "commons-collections" % "commons-collections" % "3.2.1"
)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

organization := "com.edulify"

organizationName := "Edulify.com"

organizationHomepage := Some(new URL("https://edulify.com"))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo :=  Some("Archiva Managed internal Repository" at "https://archiva.debmedia.com:9100/repository/internal")

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

startYear := Some(2018)

description := "HikariCP Plugin for Play Framework 2.2.x"

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("http://edulify.github.io/play-hikaricp.edulify.com/"))



scalacOptions := Seq("-feature", "-deprecation")

playScalaSettings
