package com.bin.onetextcode.ai;

import com.bin.onetextcode.ai.tools.FileWriteTool;
import com.bin.onetextcode.exception.BusinessException;
import com.bin.onetextcode.exception.ErrorCode;
import com.bin.onetextcode.model.enums.CodeGenTypeEnum;
import com.bin.onetextcode.service.ChatHistoryService;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private PersistentChatMemoryStore persistentChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;


    //    @Bean
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(persistentChatMemoryStore)
                .maxMessages(20)
                .build();
        // 从数据库加载历史记忆到对话记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);

        return switch (codeGenType) {
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(streamingChatModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(new FileWriteTool())
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest,
                            "Error:there is no tool called" + toolExecutionRequest.name()
                    ))
                    .build();
            case HTML, MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(streamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成模式" + codeGenType.getValue());
        };
    }

    /**
     * 默认提供一个 Bean
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L, CodeGenTypeEnum.MULTI_FILE);
    }

}
