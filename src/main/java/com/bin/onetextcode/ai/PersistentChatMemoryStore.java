package com.bin.onetextcode.ai;

import com.bin.onetextcode.config.RedisService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class PersistentChatMemoryStore implements ChatMemoryStore {

    @Resource
    private RedisService redisService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 获取会话信息
        String json = redisService.get(memoryId.toString());
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 更新会话信息
        String json = ChatMessageSerializer.messagesToJson(messages);
        redisService.set(memoryId.toString(), json);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        // 删除会话
        redisService.delete(memoryId.toString());
    }
}
