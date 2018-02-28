package com.victor.image.entities

case class Orientation(id: String, name: String)

object Orientations {

  private val orientations = Map("L" -> Orientation("L", "Landscape"), "P" -> Orientation("P", "Portrait"),
    "X" -> Orientation("X", "Square")) //this can be loaded from db

  def forId(id: String): Option[Orientation] = orientations.get(id)
}
