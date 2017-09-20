package com.genitus.channel.tracker.util.audio;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Serialize<T> {
    private final ReflectData reflectData;
    private final DatumReader<T> reader;
    private final DatumWriter<T> writer;
    private BinaryDecoder decoder;
    private BinaryEncoder encoder;
    private ByteArrayOutputStream out;
    public Serialize(Class<? super T> clazz)
    {
        reflectData = new ReflectData();
        Schema schema = reflectData.getSchema(clazz);
        reader = new ReflectDatumReader<T>(schema, schema, reflectData);
        writer = new ReflectDatumWriter<T>(schema);
        out = new ByteArrayOutputStream();
        encoder = null;
        decoder = null;
    }


    public byte[] serialize(T t) throws IOException
    {
        out.reset();
        encoder = EncoderFactory.get().binaryEncoder(out, encoder);
        writer.write(t, encoder);
        encoder.flush();
        return out.toByteArray();
    }

    public T deserialize(byte[] data) throws IOException
    {
        decoder = DecoderFactory.get().binaryDecoder(data, decoder);
        return reader.read(null, decoder);
    }
}
