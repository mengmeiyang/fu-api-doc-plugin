package com.wdf.fudoc.components.message;

import com.wdf.fudoc.components.message.handler.DialogMsgExecutor;
import com.wdf.fudoc.components.message.handler.FuMsgExecutor;
import com.wdf.fudoc.components.message.handler.LinkMsgExecutor;
import com.wdf.fudoc.components.message.handler.NormalMsgExecutor;
import com.wdf.fudoc.request.constants.enumtype.MessageType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangdingfu
 * @date 2022-11-30 17:45:05
 */
public class FuMsgHandler {

    private static final Map<MessageType, FuMsgExecutor> FU_MSG_EXECUTOR_MAP = new ConcurrentHashMap<>();

    static {
        FU_MSG_EXECUTOR_MAP.put(MessageType.NORMAL, new NormalMsgExecutor());
        FU_MSG_EXECUTOR_MAP.put(MessageType.LINK, new LinkMsgExecutor());
        FU_MSG_EXECUTOR_MAP.put(MessageType.DIALOG, new DialogMsgExecutor());
        FU_MSG_EXECUTOR_MAP.put(MessageType.CLICK, new NormalMsgExecutor());
    }

    public static FuMsgExecutor get(MessageType messageType) {
        return FU_MSG_EXECUTOR_MAP.get(messageType);
    }
}
