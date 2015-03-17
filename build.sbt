name := "stripe-scala"
version := io.Source.fromFile("VERSION").mkString.trim
organization := "com.stripe"
scalaVersion := "2.11.6"
scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "[4.1, 4.2)",
  "net.liftweb" %% "lift-json" % "2.6-RC2",
  "com.lihaoyi" %% "utest" % "0.3.1",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test")

testFrameworks += new TestFramework("utest.runner.Framework")
