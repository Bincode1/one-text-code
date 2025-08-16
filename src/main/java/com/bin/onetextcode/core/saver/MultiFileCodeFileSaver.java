package com.bin.onetextcode.core.saver;

import cn.hutool.core.util.StrUtil;
import com.bin.onetextcode.ai.model.MultiFileCodeResult;
import com.bin.onetextcode.exception.BusinessException;
import com.bin.onetextcode.exception.ErrorCode;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;

public class MultiFileCodeFileSaver extends CodeFileSaverTemplate<MultiFileCodeResult> {


    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String dirPath) {
        writeToFile(dirPath, "index.html", result.getHtmlCode());
        writeToFile(dirPath, "index.css", result.getCssCode());
        writeToFile(dirPath, "index.js", result.getJsCode());
    }

    @Override
    protected void validInput(MultiFileCodeResult result) {
        if (StrUtil.isBlank(result.getHtmlCode())) {
            // 至少HTML文件不能为空
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 文件不能为空");
        }
    }
}
