package com.victor.image.utils

import com.typesafe.config.ConfigFactory

trait ApplicationConf {
  private val config = ConfigFactory.load()
  private val threadPool = config.getConfig("threadpool")
  private val imageHandler = config.getConfig("image-handler")

  val amazonThreadPoolSize: Int = threadPool.getInt("amazon-service-size")
  val imThreadPoolSize: Int = threadPool.getInt("im-service-size")
  val blockingThreadPoolSize: Int = threadPool.getInt("blocking-io-size")

  val temporaryFolder: String = imageHandler.getString("tmp-folder")
  val skuFormat: String = imageHandler.getString("sku-format")
}
