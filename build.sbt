name := "thrift-tutorial"

version := "0.1"

lazy val jettyVersion = "9.4.8.v20171121"

libraryDependencies ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.11.0",
  "javax.annotation" % "javax.annotation-api" % "1.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "javax.servlet" % "javax.servlet-api" % "3.1.0",
  "org.eclipse.jetty" % "jetty-server" % jettyVersion,
  "org.eclipse.jetty" % "jetty-webapp" % jettyVersion,
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

// thriftJavaEnabled := true
