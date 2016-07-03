import _root_.sbt._

conflictManager := ConflictManager.latestTime

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.6.0-SNAPSHOT")

addSbtPlugin("com.hanhuy.sbt" % "android-protify" % "1.2.0")

dependencyOverrides += "com.hanhuy.sbt" % "android-sdk-plugin" % "1.6.0-SNAPSHOT"

//addSbtPlugin("com.github.aafa" % "realm-sbt-plugin" % "0.1.4-SNAPSHOT")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

//addSbtPlugin("org.scala-android" % "sbt-android" % "1.6.3")
//
//addSbtPlugin("org.scala-android" % "sbt-android-protify" % "1.2.3")
