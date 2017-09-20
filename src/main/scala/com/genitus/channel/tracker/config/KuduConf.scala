package com.genitus.channel.tracker.config

import org.rogach.scallop.{ScallopConf, ScallopOption}

trait KuduConf extends ScallopConf{
  /** Impala  Address */
  lazy val impalaAddress: ScallopOption[String] = opt[String](
    "impala_address",
    descr = "Impala Address",
   // default = Some("172.26.5.11:21050"),
    default=Some("jwzheng"),
    required = true,
    noshort = true)
}
