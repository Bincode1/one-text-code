package com.bin.onetextcode.core.parser;

/**
 * 代码解析器接口
 *
 * @param <T>
 */
public interface CodeParser<T> {

    /**
     * 解析代码内容
     *
     * @param codeContent
     * @return
     */
    T paraseCode(String codeContent);
}
