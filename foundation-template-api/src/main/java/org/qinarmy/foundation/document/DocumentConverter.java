package org.qinarmy.foundation.document;


import org.qinarmy.foundation.data.ParamValidateException;
import org.springframework.core.io.Resource;

/**
 * 文档转换器
 * created  on 2018-12-20.
 */
public interface DocumentConverter {


    /**
     * 将 html 转换为 pdf
     */
    Resource htmlToPdf(ConvertForm form) throws ParamValidateException, DocumentConvertException;




}


