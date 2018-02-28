package com.victor.image.entities

import java.io.File

case class ImageFile(designerId: Long, designId: Long, productType: ProductType, orientation: Orientation, size: Size, file: File)

object ImageFiles {

  def of(designerId: Long, designId: Long, productTypeOpt: Option[ProductType], orientationOpt: Option[Orientation],
         sizeOpt: Option[Size], file: File): Option[ImageFile] = Seq(productTypeOpt, orientationOpt, sizeOpt).flatten match {
    case Seq(productType: ProductType, orientation: Orientation, size: Size) =>
      Some(ImageFile(designerId, designId, productType, orientation, size, file))
    case _ => None
  }
}
