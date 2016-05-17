import android.Keys._
import android.protify.Keys._
import sbt.Keys._
import sbt._

object Build extends android.AutoBuild {

  // shortcuts
  lazy val p = TaskKey[Unit]("p", "protify shortcut")

  lazy val app = {
    Project(id = "app", base = file(".")).settings(appsSettings ++ Dependencies.libs: _*)
  }

  lazy val appsSettings = Seq(
    name := "bitrader",
    versionName in Android := Some("1.0.0"),
    scalaVersion := Versions.scalaVersion,
    platformTarget in Android := Versions.platformTarget,
    proguardConfig ~= {_ filterNot Seq("-dontobfuscate", "-dontoptimize", "-dontpreverify", "-verbose").contains},

    buildTypes in Android +=("debug", Seq(
      useProguardInDebug in Android := false,
      scalacOptions ++= Seq("-feature", "-deprecation"),
      minSdkVersion in Android := "21"
    )),

    buildTypes in Android +=("release", Seq(
      useProguardInDebug in Android := true,
      proguardOptions in Android ++= Settings.proguardRelease,
//      proguardConfig ++= file("project/proguard.pro").  getLines().toSeq,
      scalacOptions ++= Seq("-feature", "-optimise"),
      minSdkVersion in Android := "14"
    )),


    run <<= run in Android,
    install <<= install in Android,
    packageDebug <<= packageDebug in Android,

    p <<= protify,

    javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions in Android := Seq("-Xmx2G -XX:MaxPermSize=702M -XX:ReservedCodeCacheSize=256 " +
      "-XX:+CMSClassUnloadingEnabled -XX:+UseCodeCacheFlushing"),

    dexShards in Android := true,
    dexMaxHeap in Android := "4G",
    packagingOptions in Android := PackagingOptions(excludes = Settings.apkExcludeStrings)

  ) ++ protifySettings


}


