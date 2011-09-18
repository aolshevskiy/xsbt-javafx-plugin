import sbt._

import Keys._

object JavaFxPluginBuild extends Build {
	def rootSettings = Seq(
		sbtPlugin := true
	)
	lazy val root = Project("root", file(".")) settings(rootSettings :_*)
	override def projects = Seq(root)
}
