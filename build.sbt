
lazy val commonSettings = Seq(
  organization := "dekkR projects",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "feedfrenzy-backend"
  )

libraryDependencies ++= {
  val akkaV       = "2.4.0"
  val akkaStreamV = "1.0"
  val scalaTestV  = "2.2.4"
  val scalaLoggingVersion = "3.1.0"
  val logbackVersion = "1.1.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.akka" % "akka-stream-experimental_2.11"         % akkaStreamV,
    "com.typesafe.akka" % "akka-http-core-experimental_2.11"      % akkaStreamV,
    "com.typesafe.akka" % "akka-http-experimental_2.11"           % akkaStreamV,
    "org.scalatest"     %% "scalatest"                            % scalaTestV % "test",
    "commons-validator" % "commons-validator"                     % "1.4.1",
    "org.jsoup"         % "jsoup"                                 % "1.8.2",
    "org.scalaj"        %% "scalaj-http"                          % "1.1.4",
    "org.specs2"        %% "specs2-core"                          % "2.3.13" % "test",
    "com.typesafe.scala-logging" %% "scala-logging"               % scalaLoggingVersion,
    "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
    "ch.qos.logback"    % "logback-classic"                       % logbackVersion
  )
}



Revolver.settings

scalacOptions in ThisBuild ++= Seq(Opts.compile.deprecation, Opts.compile.unchecked) ++
  Seq("-Ywarn-unused-import", "-Ywarn-unused", "-Xlint", "-feature")

Seq(buildInfoSettings:_*)

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](version)

buildInfoPackage := "feedfrenzy_backend"

publishMavenStyle := true

licenses := Seq(
  ("MIT", url(s"https://github.com/dekkr/${name.value}/blob/${version.value}/LICENSE")))

bintraySettings

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("dekkr")

bintray.Keys.repository in bintray.Keys.bintray := "feedfrenzy-backend"

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("microservice", "syndication")

lsSettings

LsKeys.tags in LsKeys.lsync := (bintray.Keys.packageLabels in bintray.Keys.bintray).value

externalResolvers in LsKeys.lsync := (resolvers in bintray.Keys.bintray).value

assemblyJarName in assembly := s"${name.value}-assembly-${version.value}.jar"

//assemblyMergeStrategy in assembly := {
//  case x if x.contains("org/apache/commons/collections") => MergeStrategy.first
//  case x if x.contains("com/typesafe/scalalogging/Logger") => MergeStrategy.first
//  case x =>
//    val oldStrategy = (assemblyMergeStrategy in assembly).value
//    oldStrategy(x)
//}

pomExtra :=
  <url>http://dekkr.nl</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://github.com/dekkr/${name.value}/blob/${version.value}/LICENSE</url>
      </license>
    </licenses>
    <developers>
      <developer>
        <id>plamola</id>
        <name>Matthijs Dekker</name>
        <email>projects@dekkr.nl</email>
        <organization>dekkR.nl</organization>
        <organizationUrl>http://dekkr.nl</organizationUrl>
      </developer>
    </developers>
    <scm>
      <connection>scm:git:git@github.com:dekkr/{name.value}.git</connection>
      <developerConnection>scm:git:git@github.com:dekkr/{name.value}.git</developerConnection>
      <url>git@github.com:dekkr/{name.value}.git</url>
    </scm>

//

enablePlugins(JavaServerAppPackaging)
//enablePlugins(AshScriptPlugin)

packageSummary in Docker := "feedfrenzy-backend"
dockerExposedPorts in Docker := Seq(8029) // Ports to expose from container for Docker container linking
dockerRepository := Some("dekkr") // Repository used when publishing Docker image
dockerUpdateLatest := true

mappings in Docker <+= (packageBin in Compile, sourceDirectory) map { (_, src) =>
  val conf = src / "main" / "resources" / "docker-reference.conf"
  conf -> "/opt/docker/conf/application.conf"
}

mappings in Docker <+= (packageBin in Compile, sourceDirectory) map { (_, src) =>
  val conf = src / "main" / "resources" / "logback.xml"
  conf -> "/opt/docker/conf/logback.xml"
}

bashScriptExtraDefines += """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""
bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""
bashScriptExtraDefines += """cd ${app_home}/../"""


//// removes all jar mappings in universal and appends the fat jar
//mappings in Universal := {
//  // universalMappings: Seq[(File,String)]
//  val universalMappings = (mappings in Universal).value
//  val fatJar = (assembly in Compile).value
//  // removing means filtering
//  val filtered = universalMappings filter {
//    case (file, fileName) => !fileName.endsWith(".jar")
//  }
//  // add the fat jar
//  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
//}
//
//// the bash scripts classpath only needs the fat jar
//scriptClasspath := Seq((assemblyJarName in assembly).value)
