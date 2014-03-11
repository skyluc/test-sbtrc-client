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
import rx.lang.scala.Observer

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
    val connector = new SimpleConnector(root, new IDEServerLocator())

    connector.onConnect(newSbtClientObservable(_, root))
  }

  private def newSbtClientObservable(sbtClient: SbtClient, root: File) {
    println(s"sbtClient: $sbtClient")
    val client = new SbtClientWithObservableAndCache(sbtClient)

    for {
      build <- client.buildValue
      projectName = build.projects.head.name
      baseDirectory <- client.getSettingValue[File](projectName, "baseDirectory", None)
      managedClasspath <- client.getTaskValue[Seq[Attributed[File]]](projectName, "managedClasspath", Some("compile"))
    } {
      println(s"projectName: $projectName")
      println(s"baseDirectory: $baseDirectory")
      println(s"managedClasspath: $managedClasspath")
    }
    
  }


}

