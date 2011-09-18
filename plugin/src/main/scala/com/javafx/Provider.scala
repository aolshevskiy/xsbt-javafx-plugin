package com.javafx

import sbt._

case class Provider(fxBase: File, compileRunner: CompilerRun, runner: ScalaRun) extends FxBase with Compiler with Runner
