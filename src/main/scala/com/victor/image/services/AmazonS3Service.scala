package com.victor.image.services

import java.io.File
import java.net.URL
import java.util.concurrent.Executors

import com.victor.image.utils.ApplicationConf

import scala.concurrent._
import scala.util.Random

object AmazonS3Service extends ApplicationConf{

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(amazonThreadPoolSize))

  def uploadToS3(image: File): Future[Either[(File, URL), (File, String)]] = Future {
    Left(image -> new URL(s"https://aws.amazon.com/s3/juniqe/${Random.nextString(13)}/${image.getName}"))
  }

}
