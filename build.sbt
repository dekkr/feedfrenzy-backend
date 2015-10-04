lazy val commonSettings = Seq(
  organization := "dekkR projects",
  version := "0.0.1",
  scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "feedfrenzy-backend"
  )

libraryDependencies ++= {
  val akkaV       = "2.3.12"
  val akkaStreamV = "1.0-RC2"
  val scalaTestV  = "2.2.4"
  val scalaLoggingVersion = "3.1.0"
  val slf4jVersion = "1.7.10"
  val logbackVersion = "1.1.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-scala-experimental"         % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-scala-experimental" % akkaStreamV,
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

bintray.Keys.repository in bintray.Keys.bintray := "feedr"

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("microservice", "syndication")

lsSettings

LsKeys.tags in LsKeys.lsync := (bintray.Keys.packageLabels in bintray.Keys.bintray).value

externalResolvers in LsKeys.lsync := (resolvers in bintray.Keys.bintray).value

assemblyJarName in assembly := s"${name.value}-assembly-${version.value}.jar"

//assemblyMergeStrategy in assembly := {
//  case x if x.contains("org/apache/commons/collections") => MergeStrategy.first
//  case x if x.contains("com/esotericsoftware/minlog/") => MergeStrategy.first
//  case x if x.contains("org/xmlpull/v1/XmlPullParser") => MergeStrategy.first
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
