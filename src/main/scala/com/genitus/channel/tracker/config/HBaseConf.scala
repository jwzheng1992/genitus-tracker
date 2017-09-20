package com.genitus.channel.tracker.config

import org.rogach.scallop.{ScallopConf, ScallopOption}

trait HBaseConf extends ScallopConf{
//val schema: Schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}")

  /** log schema. */
  lazy val hbaseSchema = opt[String](
    "hbase schema",
    descr = "hbase schema",
    default = Some("{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}"),
    required = true,
    noshort = true
  )




  lazy val ncZKAddress: ScallopOption[String] = opt[String](
    "nc_zookeeper_address",
    descr = "nc zookeeper Address",
    //default = Some("172.28.3.20"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )

  lazy val scZKAddress: ScallopOption[String] = opt[String](
    "sc_zookeeper_address",
    descr = "sc zookeeper Address",
  //  default = Some("172.28.131.20"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )


  lazy val table: ScallopOption[String] = opt[String](
    "table",
    descr = "table",
   // default = Some("adtevak"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )

  lazy val family: ScallopOption[String] = opt[String](
    "family",
    descr = "family",
   // default = Some("r"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )


  lazy val qualifier: ScallopOption[String] = opt[String](
    "qualifier",
    descr = "qualifier",
    // default = Some("d"),
    default=Some("jwzheng"),
    required = true,
    noshort = true
  )

}
