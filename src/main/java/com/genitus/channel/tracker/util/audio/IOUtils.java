package com.genitus.channel.tracker.util.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class IOUtils {

    public static void write(File file, byte[] data, boolean append) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            fos.write(data);
        } finally {
            close(fos);
        }
    }

    public static void close(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
