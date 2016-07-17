import android.Keys._
import sbt.Keys._
import sbt.{Resolver, _}

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
    resolvers += "mmreleases" at "https://artifactory.mediamath.com/artifactory/libs-release-global",
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),

    libraryDependencies ++= Seq(
      aar("com.github.aafa" %% "macroid-design" % "0.1.2-SNAPSHOT"),

      "com.github.orhanobut" % "logger" % "1.12",
      "com.miguelcatalan" % "materialsearchview" % "1.4.0",
      aar("com.mikepenz" % "materialdrawer" % "5.2.9"),
      aar("com.mikepenz" % "iconics-core" % "2.6.0"),
      aar("com.mikepenz" % "google-material-typeface" % "2.2.0.1.original") exclude("Android-Iconics", "library-core"),
      aar("com.mikepenz" % "fontawesome-typeface" % "4.6.0.1") exclude("Android-Iconics", "library-core"),

      // json libs
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.2",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.2",
      "com.mediamath" %% "scala-json" % "1.0",

      "ws.wamp.jawampa" % "jawampa-netty" % "0.4.1",
      "me.chrons" %% "diode" % "0.5.1",
      "me.chrons" %% "boopickle" % "1.1.3",
      "com.github.nscala-time" %% "nscala-time" % "2.12.0",
      "com.github.PhilJay" % "MPAndroidChart" % "v2.2.4",

      "me.dm7.barcodescanner" % "zxing" % "1.8.4",
      "com.squareup.okhttp3" % "okhttp" % "3.3.1",

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