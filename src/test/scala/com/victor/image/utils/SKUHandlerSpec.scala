package com.victor.image.utils

import java.io.File

import com.victor.image.entities._
import org.specs2.mutable.Specification

object SKUHandlerSpec extends Specification {
import SKUHandler._
  "SKUConverter" should {
    "convert a string in a valid ImageFile" in {
      val file1 = new File("1-2-100P-80x120.jpg")
      val file2 = new File("1-3-100P-80x120.jpg")
      convert(file1) must beLeft(ImageFile(1, 2, ProductType(100, "Poster"), Orientation("P", "Portrait"), Size(80, 120), file1))
      convert(file2) must beLeft(ImageFile(1, 3, ProductType(100, "Poster"), Orientation("P", "Portrait"), Size(80, 120), file2))
    }

    "not be able to create ImageFile" in {
      val brokenFile = new File("1-1-1-1-1-1-1-1")
      convert(brokenFile) must beRight((brokenFile, "The fileName doesn't conform the format (\\d+)-(\\d+)-(\\d+)(.)-(\\d+)x(\\d+).jpg"))
    }

    "allow to easily change the size of a file" in {
      changeSizeForFileName("1-2-100P-80x120.jpg", Size(20, 20)) mustEqual "1-2-100P-20x20.jpg"
      changeSizeForFileName("1-2-200P-80x120.jpg", Size(1, 320)) mustEqual "1-2-200P-1x320.jpg"
      changeSizeForFileName("5-2-300P-80x120.jpg", Size(21, 10)) mustEqual "5-2-300P-21x10.jpg"
      changeSizeForFileName("4-2-400P-80x120.jpg", Size(20, 440)) mustEqual "4-2-400P-20x440.jpg"
      changeSizeForFileName("3-2-500P-80x120.jpg", Size(10, 20)) mustEqual "3-2-500P-10x20.jpg"
      changeSizeForFileName("1-232-600P-80x120.jpg", Size(999, 20)) mustEqual "1-232-600P-999x20.jpg"
    }
  }
}
