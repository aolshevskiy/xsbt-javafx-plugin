package com.javafx

import java.io.File
import sbt._
import Defaults.doClean

trait Compiler {
	self: FxBase =>
	def compileRunner: CompilerRun
	def compilerClasspath = Seq("javafxc", "javafxrt").map(jar("shared", _))
	private def extraClasspath = Seq("javafx-geom", "javafx-ui-common", "javafx-ui-desktop", "javafx-ui-swing", "javafx-ext-swing", "fxdloader", "websvc", "javafx-ui-controls", "javafx-ui-charts", "javafx-common", "javafx-io").map(jar("desktop", _))
	def bootClasspath = compilerClasspath :+ jar("desktop", "rt15")
	def opt(cp: Seq[File]) = cp.mkString(File.pathSeparator)
	def opts(classpath: Seq[File], target: File, srcs: Seq[File]) = List(
		"-classpath",	opt(classpath ++ extraClasspath),
		"-bootclasspath",	opt(bootClasspath),
		"-d", target.toString) ++ srcs.map(_.toString)
	def compile(classpath: Seq[File], srcs: Seq[File], target: File, log: Logger) {		
		val fsrcs = srcs.filter(_.getName.endsWith(".fx"))		
		doClean((target ***).get, Seq())
		target.mkdir()
		if(fsrcs.isEmpty)
			return;
		log.info("Compiling " + fsrcs.size + " JavaFx source" + (if(fsrcs.size > 1)"s" else "") + " to " + target + "...")
		val args = opts(classpath, target, srcs)
		compileRunner.run("com.sun.tools.javafx.Main", compilerClasspath, args, log) match {
			case Left(output) =>
				augmentString(output).lines.foreach(m => log.error(m))
				sys.error("Compilation failed")
			case Right(output) =>
				augmentString(output).lines.foreach(m => log.info(m))
		}
	}
}

import java.io.{PrintWriter, StringWriter}
import classpath.ClasspathUtilities
import java.lang.reflect.{Method, Modifier}
import Modifier.{isPublic, isStatic}

class CompilerRun(instance: ScalaInstance, nativeTmp: File)
{
	/** Runs the class 'mainClass' using the given classpath and options using the scala runner.*/
	def run(mainClass: String, classpath: Seq[File], options: Seq[String], log: Logger) =
	{
		val wrt = new StringWriter
		val str = new PrintWriter(wrt)
	
		def execute = 
			try { run0(mainClass, classpath, options, log, str) }
			catch { case e: java.lang.reflect.InvocationTargetException => throw e.getCause }

		execute match {
			case 0 => Right(wrt.toString)
			case _ => Left(wrt.toString)
		}
	}
	private def run0(mainClassName: String, classpath: Seq[File], options: Seq[String], log: Logger, str: PrintWriter) =
	{
		log.debug("  Classpath:\n\t" + classpath.mkString("\n\t"))
		val loader = ClasspathUtilities.makeLoader(classpath, instance.loader, instance, nativeTmp)
		val main = getMainMethod(mainClassName, loader)
		invokeMain(loader, main, options, str)
	}
	private def invokeMain(loader: ClassLoader, main: Method, options: Seq[String], s: PrintWriter) =
	{
		val currentThread = Thread.currentThread
		val oldLoader = Thread.currentThread.getContextClassLoader()
		currentThread.setContextClassLoader(loader)
		try { main.invoke(null, options.toArray[String].asInstanceOf[Array[String]], s).asInstanceOf[Int] }
		finally { currentThread.setContextClassLoader(oldLoader) }
	}
	def getMainMethod(mainClassName: String, loader: ClassLoader) =
	{
		val mainClass = Class.forName(mainClassName, true, loader)
		val method = mainClass.getMethod("compile", classOf[Array[String]], classOf[PrintWriter])
		val modifiers = method.getModifiers
		if(!isPublic(modifiers)) throw new NoSuchMethodException(mainClassName + ".compile is not public")
		if(!isStatic(modifiers)) throw new NoSuchMethodException(mainClassName + ".compile is not static")
		method
	}
}

