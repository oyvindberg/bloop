package bloop.bsp.client

import java.io.InputStream
import java.io.PrintStream
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.{Future => JFuture}

import org.eclipse.lsp4j.jsonrpc.Launcher
import ch.epfl.scala.bsp4j.ScalaBuildServer
import ch.epfl.scala.bsp4j.BuildClient
import ch.epfl.scala.bsp4j.LogMessageParams
import ch.epfl.scala.bsp4j.PublishDiagnosticsParams
import ch.epfl.scala.bsp4j.ShowMessageParams
import ch.epfl.scala.bsp4j.DidChangeBuildTarget
import ch.epfl.scala.bsp4j.TaskFinishParams
import ch.epfl.scala.bsp4j.TaskProgressParams
import ch.epfl.scala.bsp4j.TaskStartParams

abstract class BspClientHandler extends BuildClient {
  // Do a no-op in these cases for now
  def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit = ()
  def onBuildTaskStart(params: TaskStartParams): Unit = ()
  def onBuildTaskProgress(params: TaskProgressParams): Unit = ()
  def onBuildTaskFinish(params: TaskFinishParams): Unit = ()
}

object BspClient {
  def connectToServer(
      clientHandler: BspClientHandler,
      clientIn: InputStream,
      clientOut: PrintStream,
      executor: ExecutorService,
      workspaceDir: Path
  ) = {
    val launcher = new Launcher.Builder[BloopBspServer]()
      .setRemoteInterface(classOf[BloopBspServer])
      //.traceMessages(new PrintWriter(System.out))
      .setInput(clientIn)
      .setOutput(clientOut)
      .setLocalService(clientHandler)
      .setExecutorService(executor)
      .create()

    val listening = launcher.startListening()
    val remoteServer = launcher.getRemoteProxy()
    new BspClientConnection(workspaceDir, listening, remoteServer)
  }
}