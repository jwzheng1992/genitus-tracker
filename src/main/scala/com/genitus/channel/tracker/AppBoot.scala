package com.genitus.channel.tracker

import com.genitus.channel.tracker.config.{ESConf, HBaseConf, HDFSConf, KuduConf}
import com.genitus.channel.tracker.module._
import com.genitus.channel.tracker.service.{ESService, HBaseService, KuduService}
import org.genitus.cardiac.http.{HttpConf, HttpModule, HttpService}
import org.genitus.cardiac.metrics.MetricsModule
import org.rogach.scallop.ScallopConf

/**
  * 程序启动入口
  * 继承老大的App
  */
object AppBoot extends org.genitus.cardiac.App{

  //收集相关参数设置
  lazy val conf = new ScallopConf(args)
    with HttpConf
    with KuduConf
    with HDFSConf
    with HBaseConf
    with ESConf

  //使用参数配置各个模块
  override def modules() = {
    Seq(
      new HttpModule(conf),
      new MetricsModule,
      new RestModule,
      new KuduModule(conf),
      new HDFSModule(conf),
      new ESModule(conf),
      new HBaseModule(conf)
    )
  }

  initConf()

  //启动各项服务
  run(classOf[HttpService])
  run(classOf[KuduService])
  run(classOf[ESService])
  run(classOf[HBaseService])
}
