package com.genitus.channel.tracker.module

import com.genitus.channel.tracker.config.{ESConf, HDFSConf}
import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory

class HDFSModule(val hdfsConf: HDFSConf) extends AbstractModule{
  override def configure(): Unit = {bind(classOf[HDFSConf]).toInstance(hdfsConf)}

  //logger
  private val log = LoggerFactory.getLogger(classOf[HDFSModule])
}
