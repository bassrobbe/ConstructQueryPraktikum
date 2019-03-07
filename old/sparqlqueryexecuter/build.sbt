import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.github.pathikrit" %% "better-files" % "3.2.0",
      // "com.chuusai" %% "shapeless" % "2.3.2",
      "org.apache.jena" % "jena-core" % "3.10.0",
      "org.apache.jena" % "jena-arq" % "3.10.0",
      "org.slf4j" % "slf4j-nop" % "1.7.25"

    )
  )
