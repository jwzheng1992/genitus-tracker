package com.genitus.channel.tracker.module



import com.genitus.channel.tracker.client.ESClient
import com.genitus.channel.tracker.config.{ESConf, KuduConf}
import com.genitus.channel.tracker.service.ESService
import com.google.inject.{AbstractModule, Provides, Singleton}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable


class ESModule(val esConf: ESConf) extends AbstractModule{
  override def configure(): Unit = {bind(classOf[ESConf]).toInstance(esConf)}

  //logger
  private val log = LoggerFactory.getLogger(classOf[ESModule])

/*
  @Provides
  @Singleton
  def provideESClient(esConf: ESConf1):Map[String,ESClient]={
    val ncESClient:ESClient = new ESClient("cluster.name",esConf.ncESClusterName(),esConf.ncESIp(),esConf.ncESPort())
    val scESClient:ESClient = new ESClient("cluster.name",esConf.scESClusterName(),esConf.scESIp(),esConf.scESPort())
    val esClientMap: Map[String, ESClient] = Map(
      "sc"->scESClient,
      "nc"->ncESClient
    )
    log.info("完成esClientMap启动")
    esClientMap
  }*/


  @Provides
  @Singleton
  def provideESClient(esConf: ESConf):mutable.Map[String,ESClient]={
    val ES_IP:Array[String] = esConf.ES_IP().split(":")
    for(i<- 0 until ES_IP.length)
      println(ES_IP(i))
    val ES_Port:Array[String] = esConf.ES_Port().split(":")
    for(i<- 0 until ES_Port.length)
      println(ES_Port(i))
    val ES_Cluster_Name:Array[String] = esConf.ES_Cluster_Name().split(",")
    val ES_City:Array[String] = esConf.ES_City().split(",")
    val clusterNum=ES_IP.length;
    val esClientMap:mutable.HashMap[String,ESClient]= new mutable.HashMap[String,ESClient];


    for(i<-0 until clusterNum){
      val esClient:ESClient = new ESClient("cluster.name",ES_Cluster_Name(i),ES_IP(i),ES_Port(i))
      esClientMap += (ES_City(i)->esClient)
    }

    esClientMap


/*    val ncESClient:ESClient = new ESClient("cluster.name",esConf.ncESClusterName(),esConf.ncESIp(),esConf.ncESPort())
    val scESClient:ESClient = new ESClient("cluster.name",esConf.scESClusterName(),esConf.scESIp(),esConf.scESPort())
    val esClientMap: Map[String, ESClient] = Map(
      "sc"->scESClient,
      "nc"->ncESClient
    )
    log.info("完成esClientMap启动")
    esClientMap*/
  }

  @Provides
  @Singleton
  def provideESService( esClientMap: mutable.Map[String, ESClient]):ESService={
    val eSService:ESService = new ESService(esClientMap.asJava);
    eSService

  }

}
