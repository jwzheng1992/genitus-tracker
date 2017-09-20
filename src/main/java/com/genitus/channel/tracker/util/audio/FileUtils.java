package com.genitus.channel.tracker.util.audio;

import java.io.File;

public class FileUtils {

    /**
     * 保存文件
     * @param filePath
     * @param filename
     * @param data
     * @param overwrite
     * @throws Exception
     */
    public static void savaFile(final String filePath, final String filename, byte[] data, boolean overwrite) throws Exception {
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            if (overwrite) {
                file.delete();
            } else {
                return;
            }
        }
        IOUtils.write(file, data, false);
    }
}
