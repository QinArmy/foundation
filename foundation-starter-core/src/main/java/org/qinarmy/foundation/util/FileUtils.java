package org.qinarmy.foundation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.UUID;

/**
 * created  on 2019-03-17.
 */
public abstract class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 获取临时目录.这个目录将跟日期有关
     */
    public static File getTempDirWithDate(){
        String dirName = "temp-space/"
                + YearMonth.now().format(TimeUtils.YEAR_MONTH_FORMATTER)
                + "/" + MonthDay.now().format(TimeUtils.MONTH_DAY_FORMATTER);
        File directory = new File(SystemUtils.getTempDir(), dirName);

        if (!directory.exists() && directory.mkdirs()) {
            LOG.trace("create temp dir {}", directory.getAbsolutePath());
        }
        return directory;
    }

    /**
     *
     * @param extension 文件扩展名,如: txt,zip,或为 null ,则无
     */
    public static File createTempFile(String extension)throws IOException {

        String fileName = UUID.randomUUID().toString();
        if(StringUtils.hasText( extension )){
            fileName = fileName + "." + extension;
        }
        File file =  new File(getTempDirWithDate(),fileName);
        if(!file.exists() && file.createNewFile()){
            LOG.trace("create temp file:{}",file.getAbsolutePath());
        }
        return file;
    }

    public static void deleteFile(File file){
        if (file != null && file.exists() && file.delete()) {
            LOG.debug("delete file:{}", file.getAbsolutePath());
        }
    }
}
