package org.skyluc.sbtrc

import sbt.client.SbtClient
import sbt.client.TaskResult
import rx.lang.scala.Observable
import sbt.protocol.MinimalBuildStructure
import rx.lang.scala.Observer
import rx.lang.scala.Subscriber
import scala.concurrent.ExecutionContext.Implicits.global
import rx.lang.scala.Subscription
import rx.operators.OperationMulticast
import rx.operators.OperationReplay
import scala.concurrent.Future
import scala.concurrent.Promise
import sbt.client.TaskKey
import sbt.protocol.ScopedKey
import sbt.protocol.TaskSuccess
import sbt.protocol.TaskFailure
import sbt.client.SettingKey

class SbtClientWithObservable(client: SbtClient) {

  // Members declared in java.io.Closeable

  def close(): Unit = {
    // TODO: close all observables
    client.close()
  }

  def lookupScopedKey(name: String): scala.concurrent.Future[Seq[sbt.client.ScopedKey]] = {
    client.lookupScopedKey(name)
  }

  // Members declared in sbt.client.SbtClient

//  def handleEvents(listener: sbt.client.Event => Unit)(implicit ex: scala.concurrent.ExecutionContext): sbt.client.Subscription = ???
//
//  def possibleAutocompletions(partialCommand: String, detailLevel: Int): scala.concurrent.Future[Set[sbt.client.Completion]] = ???
//
//  def requestExecution(commandOrTask: String, interaction: Option[(sbt.client.Interaction, scala.concurrent.ExecutionContext)]): scala.concurrent.Future[Unit] = ???

  //------------

  def buildWatcher(): Observable[MinimalBuildStructure] = {
    val observable = Observable { subscriber: Subscriber[MinimalBuildStructure] =>
      val subscription = client.watchBuild { b: MinimalBuildStructure =>
        subscriber.onNext(b)
      }
      subscriber.add(Subscription {
        subscription.cancel()
      })
    }

    observable
  }
  
  def keyWatcher[T](key: TaskKey[T]): Observable[(ScopedKey, TaskResult[T])] = {
    val observable = Observable { subscriber: Subscriber[(ScopedKey, TaskResult[T])] =>
      val subscription = client.watch(key) { (scopedKey, result) =>
         subscriber.onNext((scopedKey, result))
      }
      subscriber.add(Subscription {
        subscription.cancel
      })
    }
    observable
  }

  def keyWatcher[T](key: SettingKey[T]): Observable[(ScopedKey, TaskResult[T])] = {
    val observable = Observable { subscriber: Subscriber[(ScopedKey, TaskResult[T])] =>
      val subscription = client.watch(key) { (scopedKey, result) =>
         subscriber.onNext((scopedKey, result))
      }
      subscriber.add(Subscription {
        subscription.cancel
      })
    }
    observable
  }
  
}