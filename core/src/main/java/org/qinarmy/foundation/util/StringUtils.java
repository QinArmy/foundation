package org.qinarmy.foundation.util;

/**
 * created  on 2019-03-17.
 */
public abstract class StringUtils extends org.springframework.util.StringUtils {


    /**
     * Extract the filename prefix from the given path,
     * e.g. "mypath/myfile.txt" -> "myfile".
     * e.g. "myfile.txt" -> "myfile".
     *
     * @param pathOrFileName the file path (may be {@code null})
     * @return the extracted filename, or {@code null} if none
     */
    public static String getFileNamePrefix(String pathOrFileName) {
        String filename = getFilename( pathOrFileName );
        int index = filename.lastIndexOf( '.' );
        String filenamePrefix;
        if (index <= 0) {
            // 没有扩展名或为 linux 隐藏文件
            filenamePrefix = filename;
        } else {
            filenamePrefix = filename.substring( 0, index );
        }
        return filenamePrefix;
    }


}
