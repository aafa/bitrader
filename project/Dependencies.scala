import android.Keys._
import sbt.Keys._
import sbt._

object Dependencies {

  lazy val resolverUrls =
    Seq(
      Resolver.mavenLocal,
      DefaultMavenRepository,
      Resolver.jcenterRepo,
      Resolver.defaultLocal,
      "jitpack" at "https://jitpack.io"
    )

  private val gmsVersion: String = "8.1.0"
  private val iconify: String = "2.2.2"

  lazy val libs: Seq[sbt.Setting[_]] = Seq(
    resolvers ++= resolverUrls,

    libraryDependencies ++= Seq(
      aar("com.github.aafa" %% "macroid-design" % "0.1.2-SNAPSHOT"),
      "io.github.aafa" %% "scala-retrofit" % "0.1.0-SNAPSHOT",

      "com.github.nscala-time" %% "nscala-time" % "2.12.0",
      "com.github.PhilJay" % "MPAndroidChart" % "v2.2.4",
      "com.joanzapata.iconify" % "android-iconify-fontawesome" % iconify,
      "com.joanzapata.iconify" % "android-iconify-material" % iconify
    ),

    fork in Test := true,
//    testForkedParallel := true, // todo figure out why its getting slow
    libraryDependencies ++= Seq(
      "com.geteit" %% "robotest" % "0.12" % Test,
      "org.scalatest" %% "scalatest" % "2.2.5" % Test
    )

  )

}