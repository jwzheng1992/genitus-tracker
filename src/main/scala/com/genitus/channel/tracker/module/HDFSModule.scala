package com.genitus.channel.tracker.module

import com.genitus.channel.tracker.client.{ESClient, HDFSClient}
import com.genitus.channel.tracker.config.{ESConf, HDFSConf}
import com.genitus.channel.tracker.service.HDFSService
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}
import org.slf4j.LoggerFactory
import org.apache.avro.Schema

import scala.collection.JavaConverters._
class HDFSModule(val hdfsConf: HDFSConf) extends AbstractModule{
  override def configure(): Unit = {bind(classOf[HDFSConf]).toInstance(hdfsConf)}
  //logger
  private val log = LoggerFactory.getLogger(classOf[HDFSModule])


  @Named("mediaSchema")
  @Singleton
  @Provides
  def provideMediaSchema(hdfsConf: HDFSConf):Schema ={
    val mediaSchema:Schema =   new Schema.Parser().parse(hdfsConf.mediaSchema())
    mediaSchema
  }

  @Named("dataSchema")
  @Singleton
  @Provides
  def provideDataSchema(hdfsConf: HDFSConf):Schema ={
    val dataSchema:Schema =   new Schema.Parser().parse(hdfsConf.dataSchema())
    dataSchema
  }

  @Singleton
  @Provides
  def provideHDFSClient(hdfsConf: HDFSConf,@Named("mediaSchema") mediaSchema: Schema,@Named("dataSchema") dataSchema: Schema):Map[String,HDFSClient]={
    log.info("载入HDFS config 到 HDFSClient 中")

    val scHDFSClient:HDFSClient = new HDFSClient(hdfsConf.scHDFSAddress(),mediaSchema,dataSchema );
    log.info("sc hdfs client 启动成功")

    val ncHDFSClient:HDFSClient = new HDFSClient(hdfsConf.ncHDFSAddress(),mediaSchema,dataSchema);
    log.info("nc hdfs client 启动成功")

    val hdfsClientmap: Map[String, HDFSClient] = Map(
      "sc"->scHDFSClient,
      "nc"->ncHDFSClient
    )
    log.info("完成hdfsClientMap启动")

    hdfsClientmap
  }

  @Singleton
  @Provides
  def provideHDFSService(hdfsClientMap: Map[String, HDFSClient]):HDFSService ={
    val hdfsService:HDFSService  = new HDFSService(hdfsClientMap.asJava)
    log.info("HDFS service 启动成功")
    hdfsService
  }


}
