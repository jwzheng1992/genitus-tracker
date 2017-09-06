package com.genitus.channel.tracker.module

import com.genitus.channel.tracker.config.HBaseConf
import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory

class HBaseModule(val hbaseConf: HBaseConf) extends AbstractModule{
  override def configure(): Unit = {bind(classOf[HBaseConf]).toInstance(hbaseConf)}

  //logger
  private val log = LoggerFactory.getLogger(classOf[HBaseModule])
}
