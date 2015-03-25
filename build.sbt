name := "stripe-scala"
scalaVersion := "2.11.6"
scalacOptions ++= Seq("-unchecked", "-deprecation")
resolvers += "paulp/maven" at "https://dl.bintray.com/paulp/maven"
scalacOptions ++= Seq("-Xlint", "-deprecation", "-Xfatal-warnings")

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "[4.1, 4.2)",
  "net.liftweb" %% "lift-json" % "2.6-RC2",
  "com.lihaoyi" %% "utest" % "0.3.1",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
  "org.improving" %% "expecty" % "1.0.0-RC4" % "test")

testFrameworks += new TestFramework("utest.runner.Framework")
