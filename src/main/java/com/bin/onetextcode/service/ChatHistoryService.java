package com.bin.onetextcode.service;

import com.bin.onetextcode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.bin.onetextcode.model.entity.ChatHistory;
import com.bin.onetextcode.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author 26961
 * @since 2025-08-18
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话消息
     *
     * @param appId
     * @param message
     * @param messageType
     * @param userId
     * @return
     */
    Long addChatMessage(Long appId, String message, String messageType, Long userId, Long parentId);

    /**
     * 当应用被删除后，关联删除对应的历史消息
     *
     * @param appId
     * @return
     */
    Boolean deleteByAppId(Long appId);


    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory,int maxCount);

}
