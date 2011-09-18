package com.javafx

import sbt._

trait Runner {
	self: FxBase =>
	def runner: ScalaRun
	def extraClasspath = Seq("eula", "javafx-anim", "javafx-geom", "decora-runtime", "decora-j2d", "decora-jsw", "decora-sse", "decora-d3d", "decora-j2d-rsl", "decora-ogl", "decora-j2d-jogl", "javafx-sg-common", "javafx-sg-swing", "javafx-ui-common", "javafx-ui-desktop", "javafx-ui-swing", "javafx-ext-swing", "jogl-common", "jogl-awt", "fxdloader", "jmc", "websvc", "javafx-ui-controls", "javafx-ui-charts", "script-api", "javafx-common", "javafx-io").map(jar("desktop", _)) :+ jar("shared", "javafxrt")
	def run(classpath: Seq[File], args: Array[String], log: Logger) = {
		runner.run("com.sun.javafx.runtime.Main", classpath ++ extraClasspath, args, log)
	}
}
