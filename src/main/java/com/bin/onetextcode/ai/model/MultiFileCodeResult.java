package com.bin.onetextcode.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("生成多个文件代码的结果")
@Data
public class MultiFileCodeResult {

    @Description("html代码")
    private String htmlCode;
    @Description("css代码")
    private String cssCode;
    @Description("js代码")
    private String jsCode;
    @Description("对生成的这个网站的描述")
    private String description;
}
