package com.victor.image

import java.io.File
import java.nio.file._

import com.victor.image.entities.ResultState
import com.victor.image.services._
import com.victor.image.utils.ApplicationConf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util.{Failure, Success}

object Main extends App with ApplicationConf {

  val classLoader = getClass.getClassLoader

  def getFile(s: String): File = new File(classLoader.getResource(s).getFile)

  run(Stream(getFile("images/1-2-100P-80x120.jpg"), getFile("images/1-3-100P-80x120.jpg"), getFile("images/23-2-300X-70x70.jpg")))
    .onComplete {
      case Success(smth) =>
        println(smth.succeeded.mkString("\n"))
        println(smth.failed.mkString("\n"))
      case Failure(t) => println(t)
    }

  def run(files: Stream[File]): Future[ResultState] = {
    import FlowService._
    val tempPath = Paths.get(temporaryFolder)
    FilesService.createTmpFolder(tempPath)
      .transformWith {
        case Success(tmpFolder) =>
          val copiedFiles = saveFilesToDisk(files, tmpFolder)
          val renderedFiles = renderFiles(copiedFiles)
          val uploadedToS3 = uploadToS3(renderedFiles)
          collectResultState(uploadedToS3).andThen { case _ => FilesService.deleteTmpFolder(tmpFolder) }

        case Failure(t) => Future(ResultState(Map(), Map(tempPath.toFile -> s"Couldn't create directory $tempPath: ${t.getMessage}")))
      }
  }

}
