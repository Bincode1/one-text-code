package com.bin.onetextcode;

import com.bin.onetextcode.ai.AiCodeGeneratorService;
import com.bin.onetextcode.core.AiCodeGeneratorFacade;
import com.bin.onetextcode.ai.model.MultiFileCodeResult;
import com.bin.onetextcode.ai.model.HtmlCodeResult;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class OneTextCodeApplicationTests {

    @Resource
    AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void chat() {
        HtmlCodeResult result = aiCodeGeneratorService.createSingleHtmlChat("给我生成一个个人日常日记记录的网站，要有时间线，美观,不超过100行");
        Assertions.assertNotNull(result);

    }

    @Test
    void chatMultiFile() {
        MultiFileCodeResult result = aiCodeGeneratorService.createMultiFileChat("给我生成一个留言板网站");
        Assertions.assertNotNull(result);

    }

    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode("给我生成一个商城主页", CodeGenTypeEnum.HTML);
        Assertions.assertNotNull(file);
    }


    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("给我随机生成一个网站", CodeGenTypeEnum.HTML);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
//        String s = codeStream.blockLast();
//
//        System.out.println(s);
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

}
