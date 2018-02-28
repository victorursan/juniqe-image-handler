package com.victor.image.utils

import java.io.File

import com.victor.image.entities._
import com.victor.image.services.IMService

import scala.concurrent.Future


object SKUHandler extends ApplicationConf {

  private final val skuPattern = skuFormat.r
  private final val skuSizePattern = "(\\d+)x(\\d+).jpg"

  val fileDoesntConformMessage = s"The fileName doesn't conform the format $skuPattern"

  def convert(file: File): Either[ImageFile, (File, String)] = file.getName match {
    case skuPattern(designerIdStr, designIdStr, productTypeStr, orientationStr, sizeHeightStr, sizeWidthStr) =>
      val designerId = designerIdStr.toLong
      val designId = designIdStr.toLong
      val productType = ProductTypes.of(productTypeStr.toLong)
      val orientation = Orientations.forId(orientationStr)
      val sizeHeight = sizeHeightStr.toLong
      val sizeWidth = sizeWidthStr.toLong
      val size = Sizes.forOrientation(orientation, sizeHeight, sizeWidth)

      ImageFiles.of(designerId, designId, productType, orientation, size, file) match {
        case Some(imageFile) => Left(imageFile)
        case _ => Right(file -> fileDoesntConformMessage)
      }
    case _ => Right(file -> fileDoesntConformMessage)
  }

  def renderFile(imageFile: ImageFile): Seq[Future[Either[ImageFile, (File, String)]]] =
    Sizes.smallerThan(imageFile.orientation, imageFile.size).map(size => IMService.scale(imageFile, size))


  def changeSizeForFileName(fileName: String, size: Size): String = {
    fileName.replaceAll(skuSizePattern, s"${size.height}x${size.width}.jpg")
  }
}
