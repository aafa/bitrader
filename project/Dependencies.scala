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

      aar("com.mikepenz" % "materialdrawer" % "5.2.9"),
      aar("com.mikepenz" % "iconics-core" % "2.6.0"),
      aar("com.mikepenz" % "google-material-typeface" % "2.2.0.1.original") exclude("Android-Iconics", "library-core"),
      aar("com.mikepenz" % "fontawesome-typeface" % "4.6.0.1") exclude("Android-Iconics", "library-core"),

      "io.github.aafa" %% "scala-retrofit" % "0.1.1-SNAPSHOT",

      "ws.wamp.jawampa" % "jawampa-netty" % "0.4.1"	,
      "me.chrons" %% "diode" % "0.5.1",
      "me.chrons" %% "boopickle" % "1.1.3",
      "com.github.nscala-time" %% "nscala-time" % "2.12.0",
      "com.github.PhilJay" % "MPAndroidChart" % "v2.2.4",

      "me.dm7.barcodescanner" % "zxing" % "1.8.4",

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