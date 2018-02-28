package com.victor.image.entities

import org.specs2.mutable.Specification

object SizeSpec extends Specification {
  private val portrait = Orientations.forId("P")
  private val landscape = Orientations.forId("L")
  private val square = Orientations.forId("X")
  private val undefiend = Orientations.forId("w")

  "Size" should {
    "succeed to convert valid arguments" in {
      Sizes.forOrientation(portrait, 20, 30) must beSome(Size(20, 30))
      Sizes.forOrientation(portrait, 40, 60) must beSome(Size(40, 60))
      Sizes.forOrientation(portrait, 60, 90) must beSome(Size(60, 90))
      Sizes.forOrientation(portrait, 80, 120) must beSome(Size(80, 120))
      Sizes.forOrientation(portrait, 100, 150) must beSome(Size(100, 150))

      Sizes.forOrientation(landscape, 20, 30) must beSome(Size(20, 30))
      Sizes.forOrientation(landscape, 40, 60) must beSome(Size(40, 60))
      Sizes.forOrientation(landscape, 60, 90) must beSome(Size(60, 90))
      Sizes.forOrientation(landscape, 80, 120) must beSome(Size(80, 120))
      Sizes.forOrientation(landscape, 100, 150) must beSome(Size(100, 150))

      Sizes.forOrientation(square, 20, 20) must beSome(Size(20, 20))
      Sizes.forOrientation(square, 30, 30) must beSome(Size(30, 30))
      Sizes.forOrientation(square, 50, 50) must beSome(Size(50, 50))
      Sizes.forOrientation(square, 70, 70) must beSome(Size(70, 70))
      Sizes.forOrientation(square, 100, 100) must beSome(Size(100, 100))
    }

    "return None with invalid arguments" in {
      Sizes.forOrientation(portrait, 21, 30) must beNone
      Sizes.forOrientation(landscape, 22, 30) must beNone
      Sizes.forOrientation(square, 20, 23) must beNone
      Sizes.forOrientation(undefiend, 20, 30) must beNone
    }

    "be able to retrieve all smaller Sizes" in {
      Sizes.smallerThan(portrait, Size(100, 150)) must contain(Size(20, 30), Size(40, 60), Size(60, 90), Size(80, 120), Size(100, 150))
      Sizes.smallerThan(square, Size(50, 50)) must contain(Size(20, 20), Size(30, 30), Size(50, 50))
    }

    "return Empty with invalid arguments" in {
      Sizes.smallerThan(undefiend, Size(50, 50)) must beEmpty
    }
  }
}
