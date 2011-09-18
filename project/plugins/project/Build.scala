import sbt._

object PluginDef extends Build {
	lazy val javaFxPlugin = file("plugin").toURI
	lazy val root = Project("plugins", file(".")) dependsOn(javaFxPlugin)
	override def projects = Seq(root)
}
