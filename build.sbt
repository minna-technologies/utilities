organization := "tech.minna"
name := "utilities"

scalaVersion := "2.12.6"

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

// Publishing

bintrayOrganization := Some("minna-technologies")
bintrayReleaseOnPublish in ThisBuild := true

releaseCrossBuild := true
useGpg := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
