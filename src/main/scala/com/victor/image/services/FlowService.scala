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
  Future[Stream[Either[ImageFile, (File, String)]]] = Future.sequence(
    copiedFiles.map(_.flatMap {
      case Left(imageFile: ImageFile) => Future.sequence(SKUHandler.renderFile(imageFile))
      case Right(err) => Future(Seq(Right(err)))
    })).map(_.flatten)

  def uploadToS3(renderedFiles: Future[Seq[Either[ImageFile, (File, String)]]])(implicit ec: ExecutionContext):
  Future[Seq[Either[(File, URL), (File, String)]]] =
    renderedFiles.flatMap { elements: Seq[Either[ImageFile, (File, String)]] =>
      Future.sequence(elements.map {
        case Left(imageFile) => AmazonS3Service.uploadToS3(imageFile.file)
        case Right(anyth) => Future(Right(anyth))
      })
    }

  def collectResultState(uploadedToS3: Future[Seq[Either[(File, URL), (File, String)]]])(implicit ec: ExecutionContext):
  Future[ResultState] = uploadedToS3.collect[ResultState] { case seq =>
    val (lefts, rights) = seq.foldRight((Map[File, URL](), Map[File, String]()))(
      (e, p: (Map[File, URL], Map[File, String])) => e.fold(
        (l: (File, URL)) => (p._1 + (l._1 -> l._2), p._2), (r: (File, String)) => (p._1, p._2 + (r._1 -> r._2))))
    ResultState(lefts, rights)
  }
}
