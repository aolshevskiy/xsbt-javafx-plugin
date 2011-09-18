import sbt._

import com.github.siasia.JavaFxPlugin._
import Keys._

object JavaFxBuild extends Build {
	def rootSettings = javaFxSettings ++ Seq(
		fxBase := file("/home/siasia/work/javafx-sdk1.3")
	)
	lazy val root = Project("root", file(".")) settings(rootSettings :_*)
	override def projects = Seq(root)
}
