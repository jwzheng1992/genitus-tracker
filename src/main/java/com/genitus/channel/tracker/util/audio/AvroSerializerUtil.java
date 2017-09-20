package com.genitus.channel.tracker.util.audio;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.genitus.karyo.model.data.SessionData;
import org.genitus.karyo.model.data.SessionMedia;
import org.genitus.karyo.model.log.SvcLog;
import org.genitus.lancet.util.codec.Codec;
import org.genitus.lancet.util.codec.CodecFactory;
import org.genitus.karyo.model.data.ClientPerfModel;
public class AvroSerializerUtil {
    public static GenericRecord getGenericRecordByBytes(byte[] bytes) throws Exception {
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
        byte[] bts = codec.decompress(bytes);
        Decoder decoder = DecoderFactory.get().binaryDecoder(bts, null);
        Schema schema = new Schema.Parser().parse(AvroSerializerUtil.class.getResourceAsStream("/config/schema/svc-1.avsc"));
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        return reader.read(null, decoder);
    }

    public static SvcLog getSvcLog(byte[] bytes) throws Exception {
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
        byte[] bts = codec.decompress(bytes);
        Serialize<SvcLog> serialize = new Serialize<SvcLog>(SvcLog.class);
        return serialize.deserialize(bts);
    }

    public static SessionData getSessionData(byte[] bytes) throws Exception {
        Serialize<SessionData> serialize = new Serialize<SessionData>(SessionData.class);
        return serialize.deserialize(bytes);
    }

    public static SessionMedia getSessionMedia(byte[] bytes) throws Exception {
        Serialize<SessionMedia> serialize = new Serialize<SessionMedia>(SessionMedia.class);
        return serialize.deserialize(bytes);
    }

    public static ClientPerfModel getClientPerfModel(byte[] bytes) throws Exception {
        Serialize<ClientPerfModel> serialize = new Serialize<ClientPerfModel>(ClientPerfModel.class);
        return serialize.deserialize(bytes);
    }

}
