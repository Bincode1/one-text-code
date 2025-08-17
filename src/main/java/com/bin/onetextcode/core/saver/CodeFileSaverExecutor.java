package com.bin.onetextcode.core.saver;

import com.bin.onetextcode.ai.model.HtmlCodeResult;
import com.bin.onetextcode.ai.model.MultiFileCodeResult;
import com.bin.onetextcode.exception.BusinessException;
import com.bin.onetextcode.exception.ErrorCode;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据不同的代码生成类型选择不同的代码保存逻辑
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFileSaver htmlCodeFileSaver = new HtmlCodeFileSaver();

    private static final MultiFileCodeFileSaver multiFileCodeFileSaver = new MultiFileCodeFileSaver();

    /**
     * 执行代码保存，根据生成类型，选择不同的保存方式
     *
     * @param codeResult
     * @param codeGenType
     * @return
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型" + codeGenType);
        };
    }

}
