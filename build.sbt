enablePlugins(ScalaJSPlugin)

name := "effpeepi"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2"

scalaJSUseMainModuleInitializer := true