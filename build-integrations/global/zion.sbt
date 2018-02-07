unmanagedSourceDirectories in Compile ++= {
  val sbtVersion = Keys.sbtVersion.value
  if (sbtVersion.contains("-sourcedeps")) {
    val root = Option(System.getProperty("sbt.user.plugins"))
      .map(file(_).getAbsoluteFile)
      .getOrElse(sys.error("Missing `sbt.user.plugins`"))

    val bloopBaseDir = root.getParentFile.getParentFile.getParentFile.getParentFile.getAbsoluteFile
    val integrationsMainDir = bloopBaseDir / "integrations"
    val pluginMainDir = integrationsMainDir / "sbt-bloop" / "src" / "main"
    List(
      root / "src" / "main" / "scala",
      integrationsMainDir / "core" / "src" / "main" / "scala",
      pluginMainDir / "scala",
      pluginMainDir / s"scala-sbt-${Keys.sbtBinaryVersion.value}"
    )
  } else Nil
}

// Required because the damn onLoad hook is not sourcedep friendly...
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")

// Required because sbt-native-packager doesn't work well with coursier
libraryDependencies := {
  val sbtVersion = Keys.sbtVersion.value
  val deps = libraryDependencies.value
  if (sbtVersion.startsWith("1.")) {
    deps.filter(_.organization != "io.get-coursier")
  } else deps
}
