package com.github.tedtang054.union.transport.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: dengJh
 * @Date: 2024/04/22 10:13
 */
public class MsgIdGeneratorUtils {

    private static final AtomicInteger MSG_ID = new AtomicInteger(1);


    public static Integer getMsgId() {
        return MSG_ID.getAndIncrement();
    }

}
