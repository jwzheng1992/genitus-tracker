package com.genitus.channel.tracker.module

import javax.inject.Named

import com.genitus.channel.tracker.client.HBaseClient
import com.genitus.channel.tracker.config.HBaseConf
import com.genitus.channel.tracker.service.HBaseService
import com.google.inject.{AbstractModule, Provides, Singleton}
import org.apache.avro.Schema
import org.slf4j.LoggerFactory

class HBaseModule(val hbaseConf: HBaseConf) extends AbstractModule{
  override def configure(): Unit = {bind(classOf[HBaseConf]).toInstance(hbaseConf)}

  //logger
  private val log = LoggerFactory.getLogger(classOf[HBaseModule])



  @Named("hbaseSchema")
  @Singleton
  @Provides
  def provideHBaseSchema(hbaseConf: HBaseConf):Schema ={
    val hbaseSchema:Schema =   new Schema.Parser().parse(hbaseConf.hbaseSchema())
    hbaseSchema
  }

  @Provides
  @Singleton
  def provideHBaseClient(hbaseConf: HBaseConf,@Named("hbaseSchema") hbaseSchema:Schema):HBaseClient={
    log.info("HBase client 初始化")
    val hBaseClient:HBaseClient=new HBaseClient(hbaseConf.ncZKAddress(),hbaseConf.table(),hbaseConf.family(),hbaseConf.qualifier(),hbaseSchema)
    log.info("HBase client 初始化完成")
    hBaseClient
  }



  @Provides
  @Singleton
  def provideHBaseService(hBaseClient: HBaseClient,@Named("hbaseSchema") hbaseSchema:Schema):HBaseService={
    val hBaseService:HBaseService = new HBaseService(hBaseClient,hbaseSchema)
    hBaseService
  }

}
