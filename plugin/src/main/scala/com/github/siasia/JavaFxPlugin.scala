package com.github.siasia

import sbt._
import Build.data
import Keys._
import Defaults.{prefix, runMainParser, runnerInit}
import com.javafx._
import complete._
import Project.Initialize

object JavaFxPlugin extends Plugin {
	val fxBase = SettingKey[File]("fx-base")
	val fxProvider = TaskKey[Provider]("fx-provider")
	val fxSource = SettingKey[File]("fx-source")
	val fxCompilerRunner = TaskKey[CompilerRun]("fx-compiler-runner")
	val fxCompile = TaskKey[Unit]("fx-compile")
	val fxRun = InputKey[Unit]("fx-run")

	val fxRunTask = (parsedTask: TaskKey[(String,Seq[String])]) => {
		(parsedTask, fullClasspath in fxRun, fxProvider, streams) map {
			case ( (mainClass: String, args: Seq[String]), classpath: Seq[Classpath], provider: Provider, s: std.TaskStreams[_]) =>
			toError(provider.run(data(classpath), (mainClass +: args).toArray, s.log))
		}
	}
	
	def configSettings = Seq(		
		fxSource <<= sourceDirectory / "fx",
		includeFilter in fxCompile := "*.fx",
		unmanagedSources in fxCompile <<= (fxSource, includeFilter in fxCompile) map { (b, f) => (b * f).get },
		classDirectory in fxCompile <<= (crossTarget, configuration) { (outDir, conf) => outDir / (prefix(conf.name) + "fx-classes") },
		fxCompile <<= (fxProvider, fullClasspath, unmanagedSources in fxCompile, classDirectory in fxCompile, streams) map { (p, fc, ss, cd, s) => p.compile(fc.map(_.data), ss, cd, s.log) },
		fullClasspath in fxRun <+= (classDirectory in fxCompile) map (cd => cd),
		fxRun <<= InputTask(runMainParser(_, Nil))(fxRunTask)
	)
	
	def javaFxSettings: Seq[Setting[_]] =
		inConfig(Compile)(configSettings) ++
		inConfig(Test)(configSettings) ++
		inTask(fxRun)(Seq(
			fork := true,
			runner <<= runnerInit
		)) ++
		Seq(
			fullClasspath in (Test, fxRun) <+= (classDirectory in (Compile, fxCompile)) map (cd => cd),
			fxCompilerRunner <<= (scalaInstance, taskTemporaryDirectory) map ((si, tmp) => new CompilerRun(si, tmp)),
			fxProvider <<= (fxBase, fxCompilerRunner, runner in fxRun) map Provider
		)
}
