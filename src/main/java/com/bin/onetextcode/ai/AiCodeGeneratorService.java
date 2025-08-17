package com.bin.onetextcode.ai;

import com.bin.onetextcode.ai.model.HtmlCodeResult;
import com.bin.onetextcode.ai.model.MultiFileCodeResult;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;
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
}
