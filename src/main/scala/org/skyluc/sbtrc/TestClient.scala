package org.skyluc.sbtrc

import com.typesafe.sbtrc.client.SimpleConnector
import com.typesafe.sbtrc.client.AbstractSbtServerLocator
import java.io.File
import sbt.client.SbtClient
import scala.concurrent.ExecutionContext.Implicits.global
import sbt.protocol._
import sbt.client.SettingKey
import sbt.client.TaskKey
import sbt.Attributed

/** SbtServerLocator returning the bundled sbtLaunch.jar and sbt-server.properties. */
class IDEServerLocator extends AbstractSbtServerLocator {

  override def sbtLaunchJar: java.io.File = 
    new File("/home/luc/.m2/repository/org/scala-sbt/sbt-launch/0.13.2-M2/sbt-launch-0.13.2-M2.jar")

  override def sbtProperties(directory: java.io.File): java.net.URL = 
    new File("/home/luc/dev/scala-ide/sbt-remote-control/client/target/resource_managed/main/sbt-server.properties").toURI().toURL()

}

object TestClient {
  def main(args: Array[String]) {
    new TestClient().go()
  }
}

class TestClient {

  def go() {
    
    val root = new File("/home/luc/tmp/sbt/dummy-sbt-0.13")
    val connector = new SimpleConnector(root , new IDEServerLocator())

    connector.onConnect(newSbtClient(_, root))
  }
  
  private def newSbtClient(sbtClient: SbtClient, root: File) {
    println(s"sbtClient: $sbtClient")
    sbtClient.watchBuild(newBuild(_, sbtClient, root))
  }

  def newBuild(build: MinimalBuildStructure, sbtClient: SbtClient, root: File) = {
    println(s"build: $build")
    
    val project = build.projects.head
    
    println(s"project: $project")
    
    watchBaseDirectory(project, sbtClient, root)
    watchManagedClasspath(project, sbtClient, root)
  }

  def watchBaseDirectory(project: ProjectReference, sbtClient: SbtClient, root: File) = {
    val aKey: AttributeKey = AttributeKey("baseDirectory", TypeInfo("java.io.File"))
    val scope: SbtScope = SbtScope(Some(root.toURI()), Some(project), None, None)
    val sKey: ScopedKey = ScopedKey(aKey, scope)
    val key: SettingKey[File] = SettingKey(sKey)

    println(s"key: $key")
    
    sbtClient.watch(key) { (a, b) =>
      b match {
        case TaskSuccess(value) =>
          println(s">>>>>>${value.value.get}>>>>>${value.stringValue}")
        case TaskFailure(msg) =>
          println(s">>>>>>$msg")
      }
    }

  }

  def watchManagedClasspath(project: ProjectReference, sbtClient: SbtClient, root: File) = {
    val aKey: AttributeKey = AttributeKey("managedClasspath", TypeInfo("scala.collection.Seq", Seq(TypeInfo("sbt.Attributed", Seq(TypeInfo("java.io.File"))))))
    val scope: SbtScope = SbtScope(Some(root.toURI()), Some(project), Some("compile"), None)
    val sKey: ScopedKey = ScopedKey(aKey, scope)
    val key: TaskKey[Seq[Attributed[File]]] = TaskKey(sKey)

    println(s"key: $key")
    
    sbtClient.watch(key) { (a, b) =>
      b match {
        case TaskSuccess(value) =>
          println(s">>>>>>${value.value.get}")
        case TaskFailure(msg) =>
          println(s">>>>>>$msg")
      }
    }
  }
  
}

