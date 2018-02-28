package com.victor.image.services

import java.io.File
import java.net.URL
import java.nio.file.Path

import com.victor.image.entities.{ImageFile, ResultState}
import com.victor.image.utils.SKUHandler

import scala.concurrent.{ExecutionContext, Future}

object FlowService {

  def saveFilesToDisk(files: Stream[File], tmpFolder: Path)(implicit ec: ExecutionContext):
  Stream[Future[Either[ImageFile, (File, String)]]] =
    files.map(FilesService.copyToTmpFolder(_, tmpFolder).map {
      case Left(file) => SKUHandler.convert(file)
      case Right(err) => Right(err)
    })

  def renderFiles(copiedFiles: Stream[Future[Either[ImageFile, (File, String)]]])(implicit ec: ExecutionContext):
  Stream[Future[Seq[Either[ImageFile, (File, String)]]]] = copiedFiles.map(_.flatMap {
    case Left(imageFile: ImageFile) => Future.sequence(SKUHandler.renderFile(imageFile))
    case Right(err) => Future(Seq(Right(err)))

  })

  def uploadToS3(renderedFiles: Seq[Future[Seq[Either[ImageFile, (File, String)]]]])(implicit ec: ExecutionContext):
  Future[Seq[Either[(File, URL), (File, String)]]] = Future.sequence(renderedFiles.map(
    _.flatMap { elements: Seq[Either[ImageFile, (File, String)]] =>
      Future.sequence(elements.map {
        case Left(imageFile) => AmazonS3Service.uploadToS3(imageFile.file)
        case Right(err) => Future(Right(err))
      })
    })).map(_.flatten)

  def collectResultState(uploadedToS3: Future[Seq[Either[(File, URL), (File, String)]]])(implicit ec: ExecutionContext):
  Future[ResultState] = uploadedToS3.collect[ResultState] { case seq =>
    val (succeeded, failed) = seq.foldRight((Map[File, URL](), Map[File, String]())) { (either, collector) =>
      val (succeed, fail) = collector
      either.fold((left: (File, URL)) => (succeed + left, fail),
        (right: (File, String)) => (succeed, fail + right))
    }
    ResultState(succeeded, failed)
  }
}
