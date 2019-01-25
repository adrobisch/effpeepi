enablePlugins(ScalaJSPlugin)

name := "effpeepi"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  "org.typelevel" %% "cats-effect" % "1.2.0",
  "co.fs2" %%% "fs2-core" % "1.0.1"
)

scalaJSUseMainModuleInitializer := true

scalaJSLinkerConfig in (Compile, fastOptJS) ~= { _.withSourceMap(true) }