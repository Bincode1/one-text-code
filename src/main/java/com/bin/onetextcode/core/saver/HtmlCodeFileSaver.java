package com.bin.onetextcode.core.saver;

import cn.hutool.core.util.StrUtil;
import com.bin.onetextcode.ai.model.HtmlCodeResult;
import com.bin.onetextcode.exception.BusinessException;
import com.bin.onetextcode.exception.ErrorCode;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;

public class HtmlCodeFileSaver extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }


    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validInput(HtmlCodeResult result) {
        super.validInput(result);
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }

}
