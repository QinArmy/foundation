package org.qinarmy.foundation.document;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.qinarmy.army.util.Pair;
import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.data.ParamValidateException;
import org.qinarmy.foundation.util.FileUtils;
import org.qinarmy.foundation.util.ResourceUtils;
import org.qinarmy.foundation.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.qinarmy.foundation.util.Assert.assertFalse;

/**
 * 这个类是 {@link DocumentConverter } 的一个实现
 * created  on 2018-12-20.
 *
 * @see DocumentFontProvider
 */
@Component("documentConverter")
public class DocumentConverterImpl implements ApplicationContextAware, DocumentConverter, EnvironmentAware {

    private static final String JPG = "jpg";

    private static final String PDF_DPI = "qinarmy.document.convert.pdf.dpi";

    private static final Logger LOG = LoggerFactory.getLogger(DocumentConverterImpl.class);

    private ApplicationContext applicationContext;

    private Environment env;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }


    @Override
    public Resource htmlToPdf(ConvertForm form) throws ParamValidateException, DocumentConvertException {
        assertForm(form);

        try {
            File tempFile = createTempFile();

            html2Pdf(getResource(form), Collections.emptyList(), new FileOutputStream(tempFile));

            return new FileSystemResource(tempFile);
        } catch (IOException e) {
            throw new DocumentConvertException(ResultCode.DOCUMENT_CONVERT, e.getMessage());
        }
    }

    //@Override
    public Resource htmlToImage(ConvertForm form) throws ParamValidateException, DocumentConvertException {
        assertForm(form);
        return null;
    }

    //@Override
    public Resource pdfToImage(ConvertForm form) throws ParamValidateException, DocumentConvertException {
        assertForm(form);
        File pdf = null;
        try {
            pdf = createTempFile();
            final File image = createTempFile();
            html2Pdf(getResource(form), Collections.emptyList(), new FileOutputStream(pdf));
            pdf2Image(new FileInputStream(pdf), new FileOutputStream(image));

            return new InputStreamResource(
                    new FileInputStream(image) {

                        @Override
                        public void close() throws IOException {
                            super.close();
                            if (image.exists() && image.delete()) {
                                LOG.debug("delete temp file : {}", image.getAbsolutePath());
                            }
                        }
                    }
            );
        } catch (IOException e) {
            throw new DocumentConvertException(ResultCode.DOCUMENT_CONVERT, e.getMessage(), e);
        } finally {
            if (pdf != null && pdf.exists() && pdf.delete()) {
                LOG.debug("delete temp file : {}", pdf.getAbsolutePath());
            }
        }
    }

    /*############################ 以下是非接口方法 #############################################*/

    private void assertForm(ConvertForm form) throws ParamValidateException {
        boolean bothNull = form.getResource() == null && !StringUtils.hasText(form.getResourcePattern());
        assertFalse(bothNull, "resource and resourcePattern both is null");
    }


    private Resource getResource(ConvertForm form) throws DocumentConvertException {
        try {
            Resource resource;
            if (form.getResource() != null) {
                resource = form.getResource();
            } else {
                resource = ResourceUtils.getResources(form.getResourcePattern())[0];
            }
            return resource;
        } catch (IOException e) {
            throw new DocumentConvertException(ResultCode.DOCUMENT_CONVERT, e.getMessage(), e);
        }
    }

    private void pdf2Image(InputStream pdfIn, OutputStream imageOut) throws DocumentConvertException {
        int dpi = env.getProperty(PDF_DPI, Integer.class, 300);
        try (PDDocument document = PDDocument.load(pdfIn); OutputStream out = imageOut) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int count = document.getNumberOfPages();
            BufferedImage bim;

            for (int i = 0; i < count; i++) {
                // note that the page number parameter is zero based
                bim = pdfRenderer.renderImageWithDPI(count, dpi, ImageType.RGB);

                // suffix in filename will be used as the file format
                ImageIOUtil.writeImage(bim, JPG, out, dpi);
            }
        } catch (Exception e) {
            throw new DocumentConvertException(ResultCode.DOCUMENT_CONVERT, e.getMessage(), e);
        }
    }

    private void html2Pdf(Resource html, List<Pair<String, Resource>> inlineList, OutputStream output)
            throws DocumentConvertException {

        // 提供者组件不一定是单例. 所以以这个方式获取
        DocumentFontProvider fontProvider = applicationContext.getBean(DocumentFontProvider.class);

        try (OutputStream out = output) {

            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, html.getInputStream()
                    , StandardCharsets.UTF_8, fontProvider);
            document.close();
        } catch (Exception e) {
            throw new DocumentConvertException(ResultCode.DOCUMENT_CONVERT, e.getMessage(), e);
        }

    }


    private File createTempFile() throws IOException {

        File file = new File(FileUtils.getTempDirWithDate(), UUID.randomUUID().toString());
        if (!file.exists() && file.createNewFile()) {
            LOG.debug("create temp file : {}", file.getAbsolutePath());
        }
        return file;
    }





    /*############################## 以下是内部类 #############################################*/


    private boolean notFoundFont(Font font) {
        return font == null || font.getBaseFont() == null;
    }




    /*############################## 以下是依赖 setter #############################################*/


}
