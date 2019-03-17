package org.qinarmy.foundation.document;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontProvider;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import org.qinarmy.foundation.concurrent.DistributeLock;
import org.qinarmy.foundation.concurrent.LockOption;
import org.qinarmy.foundation.eamil.EmailForm;
import org.qinarmy.foundation.eamil.EmailSender;
import org.qinarmy.foundation.util.ArrayUtils;
import org.qinarmy.foundation.util.StringUtils;
import org.qinarmy.foundation.util.SystemUtils;
import org.qinarmy.sys.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.qinarmy.foundation.util.Assert.assertHasText;

/**
 * 这个类是 {@link FontProvider} 的一个实现.
 * 这个类有以下特性 :
 * <ul>
 * <li>若没有找到相应的字体,会异步去云端同字体库(在云存储上的一个目录)同步相应的字体.</li>
 * </ul>
 * <p>
 * 由于基类{@link XMLWorkerFontProvider} 非线程安全,故设置为原型 bean.
 * </p>
 * created  on 2018-12-20.
 *
 * @see DocumentConverter
 */
@Component("documentFontProvider")
public class DocumentFontProvider extends XMLWorkerFontProvider implements EnvironmentAware, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentFontProvider.class);

    private static final String CLOUD_FONT = "qinarmy.document.font.cloud.path";

    private static final String FONT_LIBRARY_PATH = "qinarmy.document.font.library.path";

    private static final String DEFAULT_FONT_LIBRARY_SUFFIX = ".qinarmy/document/fonts";

    private static final String FONT_SYNC_ROW_COUNT = "qinarmy.document.font.cloud.sync.rowCount";

    private static final String SYNC_OFF_DURATION = "qinarmy.document.font.cloud.sync.off.duration";

    private static final String LOCK_KEY = "qinarmy.document.font.cloud.sync.lock";

    private static final String LOCK_SECOND = "qinarmy.document.font.cloud.sync.lock.second";

    private static final String EMAIL = "qinarmy.document.font.email";


    private static final String EMAIL_INTERVAL = "qinarmy.document.font.not.found.interval.minute";

    private static final String EMAIL_INTERVAL_KEY = "qinarmy.document.font.interval";


    /**
     * linux 上的字体库
     */
    private static final String[] LINUX_FONT_LIB = {"/usr/share/font", "/usr/local/share/fonts"};

    /**
     * mac 上的字体库
     */
    private static final String[] MAC_FONT_LIB = {"/System/Library/Fonts/", "/Library/Fonts/",
            "/Network/Library/Fonts/", "/System Folder/Fonts/",
            System.getProperty("user.home") + "/Library/Fonts/"
    };

    /**
     * windows 上的字体库
     */
    private static final String[] WINDOWS_FONT_LIB = {"c:/Windows/Fonts"};


    /**
     * 占位符为 {@link OS#name()}
     */
    private static final String OS_FONT_LIB = "qinarmy.document.font.%s.library";

    /**
     * itext 支持的字体文件后缀.
     */
    private static final Set<String> FONT_FILE_EXTENSIONS = ArrayUtils.asUnmodifiableSet(
            "ttf", "otf", "ttc", "afm", "pfm"
    );


    private Environment env;



    protected DistributeLock distributeLock;

    protected EmailSender emailSender;



    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assertHasText(env.getProperty(CLOUD_FONT), "未配置云字体库,可能影响文档转换.[%s]", CLOUD_FONT);
        // 注册字体库
        registerFontLibrary();
    }

    @Override
    public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, BaseColor color) {
        if (fontName == null) {
            throw new NotFoundFontException("文档没有指定样式字体,不能转换.");
        }
        Font font = super.getFont(fontName, encoding, embedded, size, style, color);
        if (notFoundFont(font)) {
            syncFontLibrary();
            throw throwNotFoundFont(fontName, style);
        }
        return font;
    }

    @Override
    public Font getFont(String fontName, String encoding, float size, int style) {
        if (fontName == null) {
            throw new NotFoundFontException("文档没有指定样式字体,不能转换.");
        }
        Font font = super.getFont(fontName, encoding, size, style);
        if (notFoundFont(font)) {
            syncFontLibrary();
            throw throwNotFoundFont(fontName, style);
        }
        return font;
    }

    private boolean notFoundFont(Font font) {
        return font == null || font.getBaseFont() == null;
    }

    private NotFoundFontException throwNotFoundFont(String fontName, int style) {
        String styleName;
        switch (style) {
            case Font.NORMAL:
                styleName = Font.FontStyle.NORMAL.name();
                break;
            case Font.BOLD:
                styleName = Font.FontStyle.BOLD.name();
                break;
            case Font.ITALIC:
                styleName = Font.FontStyle.ITALIC.name();
                break;
            case Font.UNDERLINE:
                styleName = Font.FontStyle.UNDERLINE.name();
                break;
            default:
                styleName = String.valueOf(style);
        }
        String msg = String.format("没有找到字体[%s],风格[%s],已启动异步云存储字库同步任务.", fontName, styleName);

        long minute = env.getProperty(EMAIL_INTERVAL, Long.class, 30L);

        LockOption option = new LockOption(EMAIL_INTERVAL_KEY);
        option.setSecond(TimeUnit.MINUTES.toSeconds(minute));
        if (distributeLock.tryLock(option)) {
            sendEmail(msg, null);
        }
        return new NotFoundFontException(msg);
    }


    /**
     * 异步去云端同步字体库.
     */
    private void syncFontLibrary() {


    }




    /**
     * @param fontPath 字体在云字库的路径.
     * @return true 表示成功同步文件到本地字库
     */
    private boolean syncFont(String fontPath, File fontDir) throws Exception {
        try {

            return false;
        } catch (Exception e) {
            throw new RuntimeException(String.format("cloud font path[%s] sync error,", fontPath), e);
        }
    }



    private File getFontLibrary() throws IOException {
        File file = new File(env.getProperty(FONT_LIBRARY_PATH, defaultFontLibraryPath()));
        if (!file.exists() && file.mkdirs()) {
            LOG.info("create font library,{}", file.getAbsolutePath());
        }
        return file;
    }

    private String defaultFontLibraryPath() {
        String path = System.getProperty("user.home");
        if (path.endsWith("/")) {
            path += DEFAULT_FONT_LIBRARY_SUFFIX;
        } else {
            path = path + "/" + DEFAULT_FONT_LIBRARY_SUFFIX;
        }
        return path;
    }

    /**
     * 注册操作系统和自定义字体库
     */
    private void registerFontLibrary() throws IOException {
        int total = 0, count;
        for (String fontLib : getFontPathArray()) {
            // 注册 操作系统字体库
            count = doRegisterFontLibrary(fontLib);
            LOG.info("fontLib[{}] 目录注册注册成功字体[{}]", fontLib, count);
            total += count;
        }
        // 注册本地字库
        String fontLib = getFontLibrary().getAbsolutePath();
        count = doRegisterFontLibrary(fontLib);
        LOG.info("fontLib[{}] 目录注册注册成功字体[{}]", fontLib, count);
        total += count;
        LOG.info("注册成功字体[{}]", total);
    }

    private int doRegisterFontLibrary(String fontLib) {
        LOG.info("fontLib:{}", fontLib);
        File dir = new File(fontLib);
        String[] nameArray = dir.list();
        if (!dir.exists() || !dir.isDirectory() || nameArray == null || nameArray.length == 0) {
            return 0;
        }
        int count = 0;
        File file;
        String alias, extension;

        for (String pathName : nameArray) {
            file = new File(fontLib, pathName);
            if (file.isDirectory()) {
                continue;
            }
            alias = StringUtils.getFileNamePrefix(file.getName());
            extension = StringUtils.getFilenameExtension(file.getName());

            if (!FONT_FILE_EXTENSIONS.contains(extension)) {
                continue;
            }
            if ("ttc".equals(extension)) {
                alias = null;
            }
            if (registerFont(file, alias)) {
                count++;
            }
        }
        return count;
    }

    private boolean registerFont(File fontFile, String alias) {
        boolean success;
        try {
            // 注册单个字体
            super.register(fontFile.getAbsolutePath(), alias);
            success = true;
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    private String[] getFontPathArray() {
        String key = String.format(OS_FONT_LIB, SystemUtils.localOs().name());

        String[] fontLib;
        switch (SystemUtils.localOs()) {
            case LINUX:
                fontLib = LINUX_FONT_LIB;
                break;
            case MAC:
                fontLib = MAC_FONT_LIB;
                break;
            case WINDOWS:
                fontLib = WINDOWS_FONT_LIB;
                break;
            default:
                fontLib = ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return env.getProperty(key, String[].class, fontLib);
    }


    private void sendEmail(final String msg, final Throwable e) {
        EmailForm form = new EmailForm()

                .setPrefix(EMAIL)
                .setText(msg)

                .setDateTime(LocalDateTime.now())
                .setSubject(String.format("云端字体同步,%s", msg))
                .setE(e);

        emailSender.send(form);

    }

    /*############################## 以下是 依赖 setter ############################################*/


    @Autowired
    public void setDistributeLock(@Qualifier("redisDistributeLock") DistributeLock distributeLock) {
        this.distributeLock = distributeLock;
    }

    @Autowired
    public void setEmailSender(@Qualifier("emailSender") EmailSender emailSender) {
        this.emailSender = emailSender;
    }


}
