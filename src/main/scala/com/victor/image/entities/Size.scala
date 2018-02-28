package com.victor.image.entities

case class Size(height: Long, width: Long) extends Ordered[Size] {

  override def compare(that: Size): Int = height * width compare that.width * that.height
}

object Sizes {

  private val portraitsLandscape = Seq(Size(20, 30), Size(40, 60), Size(60, 90), Size(80, 120), Size(100, 150))
  private val squares = Seq(Size(20, 20), Size(30, 30), Size(50, 50), Size(70, 70), Size(100, 100))

  private val sizes = Map[Orientation, Seq[Size]](
    Orientation("L", "Landscape") -> portraitsLandscape,
    Orientation("P", "Portrait") -> portraitsLandscape,
    Orientation("X", "Square") -> squares) //this can be loaded from db

  def forOrientation(orientationOpt: Option[Orientation], height: Long, width: Long): Option[Size] =
    orientationOpt.flatMap(forOrientation(_, height, width))

  def forOrientation(orientation: Orientation, height: Long, width: Long): Option[Size] = sizes.get(orientation)
    .map(_.find(isSameSize(_, height, width))).get

  private def isSameSize(size: Size, height: Long, width: Long): Boolean = Size.unapply(size).contains((height, width))

  def smallerThan(orientation: Orientation, size: Size): Seq[Size] = sizes.getOrElse(orientation, Nil).filter(_ <= size)

  def smallerThan(orientationOpt: Option[Orientation], size: Size): Seq[Size] = orientationOpt match {
    case Some(orientation) => smallerThan(orientation, size)
    case _ => Nil
  }

}
