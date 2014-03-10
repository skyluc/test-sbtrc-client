package org.skyluc.sbtrc

import sbt.client.SbtClient
import sbt.client.TaskResult

class SbtClientWithObservable extends SbtClient {
  
  // Members declared in java.io.Closeable
  
  def close(): Unit = ???
  
  // Members declared in sbt.client.SbtClient
  
  def handleEvents(listener: sbt.client.Event => Unit)(implicit ex: scala.concurrent.ExecutionContext): sbt.client.Subscription = ???
  
  def lookupScopedKey(name: String): scala.concurrent.Future[Seq[sbt.client.ScopedKey]] = ???
  
  def possibleAutocompletions(partialCommand: String,detailLevel: Int): scala.concurrent.Future[Set[sbt.client.Completion]] = ???
  
  def requestExecution(commandOrTask: String,interaction: Option[(sbt.client.Interaction, scala.concurrent.ExecutionContext)]):scala.concurrent.Future[Unit] = ???
  
  def watch[T](key: sbt.client.TaskKey[T])(l: (sbt.client.ScopedKey, sbt.client.TaskResult[T]) => Unit)(implicit ex: scala.concurrent.ExecutionContext): sbt.client.Subscription = ???
  
  def watch[T](key: sbt.client.SettingKey[T])(listener: (sbt.client.ScopedKey, sbt.client.TaskResult[T]) => Unit)(implicit ex: scala.concurrent.ExecutionContext): sbt.client.Subscription = ???
  
  def watchBuild(listener: sbt.client.MinimalBuildStructure => Unit)(implicit ex: scala.concurrent.ExecutionContext): sbt.client.Subscription = ???

}