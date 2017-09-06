package com.genitus.channel.tracker.module

import com.genitus.channel.tracker.config.{ESConf, KuduConf}
import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory

class ESModule(val esConf: ESConf) extends AbstractModule{
  override def configure(): Unit = {bind(classOf[ESConf]).toInstance(esConf)}

  //logger
  private val log = LoggerFactory.getLogger(classOf[ESModule])
}
