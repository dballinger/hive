name := "hive"

version := "1.0"

scalaVersion := "2.11.12"

lazy val circeVersion = "0.8.0"

lazy val circe =  Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

lazy val deps = Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.typelevel" %% "cats-core" % "0.9.0"
) ++ circe

lazy val testDeps = Seq(
  "com.github.tomakehurst" % "wiremock" % "2.12.0",
  "org.scalatest" %% "scalatest" % "3.0.4"
).map(_ % Test)

libraryDependencies ++= deps ++ testDeps