object Settings {

  lazy val apkExcludeStrings: Seq[String] = Seq(
    "META-INF/INDEX.LIST",
    "META-INF/io.netty.versions.properties",
    "META-INF/services/com.fasterxml.jackson.databind.Module",
    "META-INF/LICENSE.txt",
    "META-INF/LICENSE",
    "META-INF/NOTICE.txt",
    "META-INF/NOTICE",
    ".readme",
    "reference.conf"
  )

  lazy val proguardDebug = Seq(
    "-dontobfuscate",
    "-dontoptimize",
    "-dontpreverify"
  ) 

  lazy val proguardRelease = Seq(
    "-optimizations class/*,field/*,method/*,code/*",
    "-mergeinterfacesaggressively",
    "-dontwarn",
    "-optimizationpasses 5"
  ) 


  lazy val proguardCache = Seq(
  )
}
