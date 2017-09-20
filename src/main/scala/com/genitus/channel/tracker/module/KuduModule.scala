package com.genitus.channel.tracker.module

import com.genitus.channel.tracker.client.KuduClient
import com.genitus.channel.tracker.config.KuduConf
import com.genitus.channel.tracker.service.KuduService
import com.google.inject.{AbstractModule, Provides, Singleton}
import org.slf4j.LoggerFactory

class KuduModule(val kuduConf: KuduConf) extends AbstractModule {
  override def configure(): Unit = {bind(classOf[KuduConf]).toInstance(kuduConf)}

  //logger
  private val log = LoggerFactory.getLogger(classOf[KuduModule])

  //kudu client
  @Singleton
  @Provides
  def provideKuduClient(kuduConf: KuduConf): KuduClient = {
    log.info("载入kudu config 到 kuduClient 中")
    val kuduClient:KuduClient = new KuduClient(kuduConf.impalaAddress())
    log.info("kudu client 启动成功")
    kuduClient
  }

/*  //kudu service
  @Singleton
  @Provides
  def provideKuduService(kuduClient: KuduClient): KuduService = {
   val kuduService:KuduService  = new KuduService(kuduClient)
    log.info("kudu service 启动成功")
    kuduService
  }*/

  //kudu service
  @Singleton
  @Provides
  def provideKuduService(kuduClient: KuduClient): KuduService = {
    val kuduService:KuduService  = new KuduService(kuduClient)
    log.info("kudu service 启动成功")
    kuduService
  }

}
