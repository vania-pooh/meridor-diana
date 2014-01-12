import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object DianaBuild extends Build {
  val Organization = "ru.meridor"
  val Name = "Diana"
  val Version = "0.1.0"
  val ScalaVersion = "2.10.0"
  val ScalatraVersion = "2.2.2"

  lazy val project = Project (
    "diana",
    file("."),
    settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s"   %% "json4s-jackson" % "3.2.4",
        "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
        "com.jolbox" % "bonecp" % "0.7.1.RELEASE",
        "com.typesafe.slick" % "slick_2.10" % "1.0.0",
        "com.googlecode.flyway" % "flyway-core" % "2.1.1",
        "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
        "org.apache.commons" % "commons-io" % "1.3.2",
        "org.apache.commons" % "commons-lang3" % "3.1",
        "org.apache.httpcomponents" % "httpclient" % "4.2.5"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      },
      sourceDirectories in Compile <+= (resourceDirectory in Compile) { _ / "db" / "migration" / "java" },
      sourceDirectories in Compile <+= (resourceDirectory in Compile) { _ / "db" / "migration" / "sql" }
    )
  )
}
