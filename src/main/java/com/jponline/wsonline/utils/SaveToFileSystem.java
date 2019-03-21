package com.jponline.wsonline.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * SaveToFileSystem handle save file
 * Author: LongTH10
 */
public class SaveToFileSystem {

    public static String save(String fileName, String prefix, byte[] data) throws IOException {
        return SaveToFileSystem.save(fileName, prefix, ByteBuffer.wrap(data));
    }

    public static String save(String fileName, String prefix, ByteBuffer bufferedBytes) throws IOException {
        String path = null, folderRandom = UUID.randomUUID().toString();

        Path basePath = Paths.get(".", "uploads", prefix, folderRandom);
        Files.createDirectories(basePath);
        FileChannel channel =  new FileOutputStream(Paths.get(basePath.toString(), fileName).toFile(), false).getChannel();
        channel.write(bufferedBytes);
        channel.close();
        path = folderRandom + "/" + fileName;
        return path;
    }
}
