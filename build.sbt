organization := "tech.minna"
name := "utilities"

scalaVersion := "2.13.0"
crossScalaVersions in ThisBuild := Seq("2.12.9", "2.13.0")

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

// Publishing

bintrayOrganization := Some("minna-technologies")
bintrayReleaseOnPublish in ThisBuild := true

releaseCrossBuild := true
useGpg := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
