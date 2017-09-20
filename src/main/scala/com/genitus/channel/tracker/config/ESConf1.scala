package com.genitus.channel.tracker.config

import org.rogach.scallop.ScallopConf

trait ESConf1 extends ScallopConf{



  lazy val ncESIp = opt[String](
    "nc_ES_IP",
    descr = "nc ES IP",
  //  default = Some("172.28.4.20"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )

  lazy val scESIp = opt[String](
    "sc_ES_IP",
    descr = "sc ES IP",
    default=Some("jwzheng"),
 //   default = Some("172.28.128.14"),
    required = true,
    noshort = true
  )


  lazy val ncESPort = opt[String](
    "nc_ES_Port",
    descr = "nc ES Port",
    default=Some("jwzheng"),
 //   default = Some("9300"),
    required = true,
    noshort = true
  )

  lazy val scESPort = opt[String](
    "sc_ES_Port",
    descr = "sc ES Port",
    default=Some("jwzheng"),
  //  default = Some("9300"),
    required = true,
    noshort = true
  )
  lazy val ncESClusterName = opt[String](
    "nc_ES_cluster_name",
    descr =   "nc ES cluster name",
    default=Some("jwzheng"),
 //   default = Some("eureka"),
    required = true,
    noshort = true
  )

  lazy val scESClusterName = opt[String](
    "sc_ES_cluster_name",
    descr =   "sc ES cluster name",
    default=Some("jwzheng"),
 //   default = Some("eureka-gz"),
    required = true,
    noshort = true
  )



}
