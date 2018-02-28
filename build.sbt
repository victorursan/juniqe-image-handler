name := "juniqe-image-handler"
organization := "com.victor.image"
version := "0.1"

scalaVersion := "2.12.4"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

mainClass in Compile := Some("com.victor.image.Main")

resourceDirectory in Compile := baseDirectory.value / "src" / "main" / "resources"

libraryDependencies ++= {
  val TypeSafe = "1.3.3"
  val Specs2 = "4.0.2"

  Seq(
    //logging
    "com.typesafe" % "config" % TypeSafe,

    //test libraries
    "org.specs2" %% "specs2-core" % Specs2 % Test
  )
}
