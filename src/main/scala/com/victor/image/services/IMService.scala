package com.victor.image.services

import java.io.File
import java.util.concurrent.Executors

import com.victor.image.entities.{ImageFile, Size}
import com.victor.image.utils.{ApplicationConf, SKUHandler}

import scala.concurrent._

object IMService extends ApplicationConf {

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(imThreadPoolSize))

  def scale(imageFile: ImageFile, newSize: Size): Future[Either[ImageFile, (File, String)]] = Future {
    val oldFile = imageFile.file
    val fileName = oldFile.getName
    FilesService.renameFile(oldFile, SKUHandler.changeSizeForFileName(fileName, newSize)) match {
      case Some(newFile) =>
        Left(imageFile.copy(size = newSize, file = newFile))
      case _ => Right(oldFile, s"Could not scale file $oldFile to $newSize")
    }
  }
}
