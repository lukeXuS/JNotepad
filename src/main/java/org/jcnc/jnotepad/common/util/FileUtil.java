package org.jcnc.jnotepad.common.util;

import org.jcnc.jnotepad.controller.event.handler.menubar.OpenFile;
import org.jcnc.jnotepad.exception.AppException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件工具
 *
 * @author gewuyou
 */
public class FileUtil {
    private static final MessageDigest MESSAGE_DIGEST_SHA_256;
    private static final int BUFFER_SIZE = 8192;

    static {
        try {
            MESSAGE_DIGEST_SHA_256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AppException(e);
        }
    }

    private FileUtil() {

    }

    /**
     * 将字节数组转换为String类型哈希值
     *
     * @param bytes 字节数组
     * @return 哈希值
     */
    private static String bytes2HashCode(byte[] bytes) {
        StringBuilder hashString = new StringBuilder();
        for (byte b : bytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }


    /**
     * 获取本地文件Sha256哈希值字符串
     *
     * @param file 本地文件
     * @return 本地文件Sha256哈希值
     */
    public static String getLocalFileSha256HashString(File file) {
        try (
                // 获取文件输入流
                FileInputStream fileInputStream = new FileInputStream(file);
                // 获取字节流通道
                FileChannel channel = fileInputStream.getChannel()
        ) {
            // 设置8k缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                MESSAGE_DIGEST_SHA_256.update(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            throw new AppException(e);
        }
        return bytes2HashCode(MESSAGE_DIGEST_SHA_256.digest());
    }

    /**
     * 获取本地文件Sha256哈希值字符串
     *
     * @param pathStr 本地文件路径字符串
     * @return 本地文件Sha256哈希值
     */
    public static String getLocalFileSha256HashString(String pathStr) {
        return getLocalFileSha256HashString(new File(pathStr));
    }

    /**
     * 获取本地文件Sha256哈希值字符串
     *
     * @param path 本地文件路径
     * @return 本地文件Sha256哈希值
     */
    public static String getLocalFileSha256HashString(Path path) {
        return getLocalFileSha256HashString(path.toFile());
    }

    /**
     * 获取文件中的文本内容。
     *
     * @param file 文件对象
     * @return 文本内容
     */
    public static String getFileText(File file) {
        return getFileText(file, EncodingDetector.detectEncodingCharset(file));
    }


    /**
     * 获取文件中的文本内容。
     *
     * @param file     文件对象
     * @param encoding 文件编码
     * @return 文本内容
     */
    public static String getFileText(File file, Charset encoding) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, encoding))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(line);
            }
        } catch (IOException ignored) {
            LogUtil.getLogger(OpenFile.class).info("已忽视IO异常!");
        }
        return stringBuilder.toString();
    }
}
