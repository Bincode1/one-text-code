package com.bin.onetextcode.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Description("单文件html代码结果")
@Data
public class HtmlCodeResult {

    @Description("html代码")
    private String htmlCode;

    @Description("对生成的这个网站的描述")
    private String description;
}
