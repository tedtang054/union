package com.github.tedtang054.union.common.kafka;

import com.github.tedtang054.union.common.properties.BaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(BaseProperties.class)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaSender {

    @Resource
    private BaseProperties properties;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 向特定主题发送消息
     *
     * @param topic      主题
     * @param payload    消息
     * @param headersMap 请求头
     */
    @Async("eventExecutor")
    public void sendToSpecifyTopic(String topic, String key, Object payload, Map<String, byte[]> headersMap) {
        topic = StringUtils.hasText(properties.getEnv()) ? topic + "_" + properties.getEnv() : topic;
        log.debug("send to specify topic : {}, key : {}, message : {}", topic, key, payload);
        if (payload == null) {
            return;
        }
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, key, payload);
        Headers headers = producerRecord.headers();
        headersMap.forEach(headers::add);
        kafkaTemplate.send(producerRecord);
    }

}