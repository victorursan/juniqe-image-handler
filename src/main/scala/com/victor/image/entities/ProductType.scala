package com.victor.image.entities

case class ProductType(id: Long, name: String)

object ProductTypes {

  private val imageTypes = Map[Long, ProductType](100l -> ProductType(100, "Poster"), 200l -> ProductType(200, "Framed Art"),
    300l -> ProductType(300, "Canvas"), 400l -> ProductType(400, "Acrylic"), 500l -> ProductType(500, "AluDibond")) //this can be loaded from db

  def of(id: Long): Option[ProductType] = imageTypes.get(id)
}
