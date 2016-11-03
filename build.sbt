name := "Transaction Sample"

version := "1.0"

organization := "org.lunary" 

libraryDependencies ++= {
  val akkaVersion = "2.4.11"
  var log4j2Version = "2.7"
  var slf4jVersion = "1.7.21"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion, 
    "com.typesafe.akka" %% "akka-http-core"  % akkaVersion, 
    "com.typesafe.akka" %% "akka-http-experimental"  % akkaVersion, 
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % akkaVersion, 
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
//    "org.slf4j"         %  "slf4j-api"       % slf4jVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "2.2.0"       % "test"
//    "org.slf4j"         %  "slf4j-ext"       % slf4jVersion  % "test",
//    "org.apache.logging.log4j" % "log4j-api" % log4j2Version  %  "test",
//    "org.apache.logging.log4j" % "log4j-core" % log4j2Version  %  "test",
//    "org.apache.logging.log4j" % "log4j-jcl" % log4j2Version  %  "test",
//    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version  %  "test"
  )
}

