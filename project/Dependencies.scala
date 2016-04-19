import android.Keys._
import sbt.Keys._
import sbt._

object Dependencies {

  lazy val resolverUrls =
    Seq(
      Resolver.mavenLocal,
      DefaultMavenRepository,
      Resolver.jcenterRepo,
      Resolver.defaultLocal
    )

  private val gmsVersion: String = "8.1.0"
  private val iconify: String = "2.2.2"

  lazy val libs: Seq[sbt.Setting[_]] = Seq(
    resolvers ++= resolverUrls,

    libraryDependencies ++= Seq(
      "com.github.aafa" %% "macroid-design" % "0.1.1-SNAPSHOT",

      "com.joanzapata.iconify" % "android-iconify-fontawesome" % iconify,
      "com.joanzapata.iconify" % "android-iconify-material" % iconify
    ),

    fork in Test := true,
    libraryDependencies ++= Seq(
      "com.geteit" %% "robotest" % "0.12" % Test,
      "org.scalatest" %% "scalatest" % "2.2.5" % Test
    )

  )

}