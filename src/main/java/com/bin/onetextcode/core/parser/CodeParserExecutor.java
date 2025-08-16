package com.bin.onetextcode.core.parser;

import com.bin.onetextcode.exception.BusinessException;
import com.bin.onetextcode.exception.ErrorCode;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;

/**
 * 解析器执行器
 * 根据传入的类型，来使用不同的解析器
 */
public class CodeParserExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    public static Object executorParser(String codeContent, CodeGenTypeEnum codeGenType) {

        return switch (codeGenType) {
            case HTML -> htmlCodeParser.paraseCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.paraseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成模式" + codeGenType);
        };
    }

}
