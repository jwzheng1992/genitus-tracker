package com.genitus.channel.tracker.config

import org.rogach.scallop.ScallopConf

trait ESConf extends ScallopConf{
  lazy val ES_IP = opt[String](
    "ES_IP",
    descr = "ES_IP",
    //  default = Some("172.28.4.20"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )

  lazy val ES_Port = opt[String](
    "ES_Port",
    descr = "ES_Port",
    default=Some("jwzheng"),
    //   default = Some("172.28.128.14"),
    required = true,
    noshort = true
  )


  lazy val ES_Cluster_Name = opt[String](
    "ES_Cluster_Name",
    descr = "ES_Cluster_Name",
    default=Some("jwzheng"),
    //   default = Some("9300"),
    required = true,
    noshort = true
  )

  lazy val ES_City = opt[String](
    "ES_City",
    descr = "ES_City",
    default=Some("jwzheng"),
    //  default = Some("9300"),
    required = true,
    noshort = true
  )


}
