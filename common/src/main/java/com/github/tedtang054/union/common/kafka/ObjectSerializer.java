package com.github.tedtang054.union.common.kafka;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * @Author: dengJh
 * @Date: 2022/04/25 14:42
 */
@Slf4j
public class ObjectSerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(String topic, Object data) {
        try {
            if (data == null) {
                return null;
            }
            if (data instanceof String) {
                return ((String) data).getBytes(StandardCharsets.UTF_8);
            }
            return JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("序列化kafka信息失败:", e);
            return null;
        }
    }

}
