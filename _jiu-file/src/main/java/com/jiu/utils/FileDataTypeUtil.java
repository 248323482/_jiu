package com.jiu.utils;


import com.jiu.entity.enumeration.DataType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 根据类型识别工具
 *
 */
@Slf4j
public class FileDataTypeUtil {
    final static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM");
    private final static String IMAGE = "image";
    private final static String VIDEO = "video";
    private final static String DIR = "application/x-director";
    private final static String AUDIO = "audio";
    private final static String TEXT = "text";

    public static void main(String[] args) {
        System.err.println(md5(encrypt("123456", "leiting")));
    }
    public static String encrypt(String paramString1, String paramString2)
    {
        Key key = getKey(paramString2.getBytes());
        try
        {
            paramString1 = byteArr2HexStr(encrypt(paramString1.getBytes(), key));
            return paramString1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static byte[] encrypt(byte[] paramArrayOfByte, Key paramKey)
            throws Exception
    {
        Cipher localCipher = Cipher.getInstance("DES");
        localCipher.init(1, paramKey);
        return localCipher.doFinal(paramArrayOfByte);
    }
    public static String byteArr2HexStr(byte[] paramArrayOfByte)
            throws Exception
    {
        int i = 0;
        int k = paramArrayOfByte.length;
        StringBuffer localStringBuffer = new StringBuffer(k * 2);
        while (i < k)
        {
            int j = paramArrayOfByte[i];
            while (j < 0) {
                j += 256;
            }
            if (j < 16) {
                localStringBuffer.append("0");
            }
            localStringBuffer.append(Integer.toString(j, 16));
            i += 1;
        }
        return localStringBuffer.toString();
    }
    private static Key getKey(byte[] paramArrayOfByte)
    {
        byte[] arrayOfByte = new byte[8];
        int i = 0;
        while ((i < paramArrayOfByte.length) && (i < arrayOfByte.length))
        {
            arrayOfByte[i] = paramArrayOfByte[i];
            i += 1;
        }
        return new SecretKeySpec(arrayOfByte, "DES");
    }
    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("");
        }
        String md5code = (new BigInteger(1, secretBytes)).toString(16);
        for (int i = 0; i < 32 - md5code.length(); ) { md5code = "0" + md5code; i++; }

        return md5code;
    }
    /**
     * 根据mine类型，返回文件类型
     *
     */
    public static DataType getDataType(String mime) {
        if (mime == null || "".equals(mime)) {
            return DataType.OTHER;
        }
        if (mime.contains(IMAGE)) {
            return DataType.IMAGE;
        } else if (mime.contains(TEXT)
                || mime.startsWith("application/vnd.ms-excel")
                || mime.startsWith("application/msword")
                || mime.startsWith("application/pdf")
                || mime.startsWith("application/vnd.ms-project")
                || mime.startsWith("application/vnd.ms-works")
                || mime.startsWith("application/x-javascript")
                || mime.startsWith("application/vnd.openxmlformats-officedocument")
                || mime.startsWith("application/vnd.ms-word.document.macroEnabled")
                || mime.startsWith("application/vnd.ms-word.template.macroEnabled")
                || mime.startsWith("application/vnd.ms-powerpoint")
        ) {
            return DataType.DOC;
        } else if (mime.contains(VIDEO)) {
            return DataType.VIDEO;
        } else if (mime.contains(DIR)) {
            return DataType.DIR;
        } else if (mime.contains(AUDIO)) {
            return DataType.AUDIO;
        } else {
            return DataType.OTHER;
        }
    }

    public static String getUploadPathPrefix(String uploadPathPrefix) {
        //日期文件夹
        String secDir = LocalDate.now().format(DTF);
        // web服务器存放的绝对路径
        String absolutePath = Paths.get(uploadPathPrefix, secDir).toString();
        return absolutePath;
    }

    public static String getRelativePath(String pathPrefix, String path) {
        String remove = StringUtils.remove(path, pathPrefix + File.separator);

        log.info("remove={}, index={}", remove, remove.lastIndexOf(File.separator));
        String relativePath = StringUtils.substring(remove, 0, remove.lastIndexOf(File.separator));
        return relativePath;
    }

}
