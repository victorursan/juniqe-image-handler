package com.victor.image.services

import java.io.File
import java.nio.file._
import java.util.concurrent.Executors

import com.victor.image.utils.ApplicationConf

import scala.concurrent._
import scala.util.{Failure, Success, Try}

object FilesService extends ApplicationConf {

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(blockingThreadPoolSize))

  def createTmpFolder(tmpPath: Path): Future[Path] = Future {
    val lastMillisOfUnixTime = (System.currentTimeMillis() % 10000).toString
    Files.createDirectories(tmpPath.resolve(lastMillisOfUnixTime))
  }

  def copyToTmpFolder(file: File, tmpFolder: Path): Future[Either[File, (File, String)]] = Future {
    val filePath = file.toPath
    Try(Files.copy(filePath.toAbsolutePath, tmpFolder.resolve(filePath.getFileName))) match {
      case Success(tmpPath) => Left(tmpPath.toFile)
      case Failure(t) =>
        Right(file -> s"Couldn't save the file locally $t")
    }
  }

  def deleteTmpFolder(root: Path): Unit = {
    for (file <- root.toFile.listFiles()) file.delete()
    root.toFile.delete()
  }

  def renameFile(file: File, newName: String): Option[File] = Try(
    Files.copy(file.toPath, file.toPath.resolveSibling(newName)).toFile).toOption

}
