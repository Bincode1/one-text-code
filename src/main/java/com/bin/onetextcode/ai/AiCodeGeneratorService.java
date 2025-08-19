package com.bin.onetextcode.ai;

import com.bin.onetextcode.ai.model.HtmlCodeResult;
import com.bin.onetextcode.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    @SystemMessage(fromResource = "prompt/singleHtml.txt")
    HtmlCodeResult createSingleHtmlChat(String userMessage);


    @SystemMessage(fromResource = "prompt/multiFile.txt")
    MultiFileCodeResult createMultiFileChat(String userMessage);


    @SystemMessage(fromResource = "prompt/singleHtml.txt")
    Flux<String> createSingleHtmlStreamChat(String userMessage);


    @SystemMessage(fromResource = "prompt/multiFile.txt")
    Flux<String> createMultiFileStreamChat(String userMessage);

    /**
     * 生成 Vue 项目代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成过程的流式响应
     */
    @SystemMessage(fromResource = "prompt/vue_project.txt")
    Flux<String> generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);

}
