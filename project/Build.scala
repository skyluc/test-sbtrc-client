import sbt._
import Keys._

object ProjectBuild extends Build {

  val baseSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.skyluc",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := "2.10.2",
    libraryDependencies ++= Seq(
      "com.typesafe.sbtrc" % "client" % "1.0-2ed540099de878afad149a1b86d395ab13f92f53",
      "com.netflix.rxjava" % "rxjava-scala" % "0.17.0-RC7"
    )
  )

  lazy val project = Project ("test-sbtrc-client", file("."), settings = baseSettings)

}
