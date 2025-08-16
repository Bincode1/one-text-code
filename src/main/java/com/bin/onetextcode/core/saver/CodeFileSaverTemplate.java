package com.bin.onetextcode.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.bin.onetextcode.exception.ErrorCode;
import com.bin.onetextcode.exception.ThrowUtils;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    public final File saveCode(T result) {
        // 验证输入
        validInput(result);
        // 构建唯一保存路径
        String baseDirPath = buildUniqueDir();
        // 保存文件
        saveFiles(result, baseDirPath);
        //返回文件
        return new File(baseDirPath);
    }


    /**
     * 验证输入的有效性
     *
     * @param result
     */
    protected void validInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.PARAMS_ERROR);
    }

    protected String buildUniqueDir() {
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }


    protected void writeToFile(String dirPath, String fileName, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + fileName;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取代码类型，具体实现由子类实现
     *
     * @return
     */
    protected abstract CodeGenTypeEnum getCodeType();

    protected abstract void saveFiles(T result, String dirPath);


}
