package com.bin.onetextcode.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum ChatHistoryMessageTypeEnum {

    USER("用户", "user"),
    AI("助手", "ai");

    private final String text;
    private final String value;

    ChatHistoryMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static String getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (ChatHistoryMessageTypeEnum valueEnum : ChatHistoryMessageTypeEnum.values()) {
            if (valueEnum.value.equals(value)) {
                return valueEnum.text;
            }
        }
        return null;
    }
}
