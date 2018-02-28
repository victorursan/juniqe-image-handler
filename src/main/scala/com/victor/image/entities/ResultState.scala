package com.victor.image.entities

import java.io.File
import java.net.URL

case class ResultState(succeeded: Map[File, URL], failed: Map[File, String])
