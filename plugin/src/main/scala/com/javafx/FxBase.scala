package com.javafx

import sbt._

trait FxBase {
	def fxBase: File
	def jar(profile: String, name: String):File = fxBase / "lib" / profile / (name + ".jar")
}
