package com.github.tedtang054.union.common.kafka;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: dengJh
 * @Date: 2023/05/05 14:49
 */
public interface KafkaConstant {

    String PUB_ALARM_TOPIC = "alarm_info";

    String PUB_INDOOR_LOCATION_TOPIC = "indoor_location";

    String PUB_BLE_BEACON_TOPIC = "ble_beacon";

    String PUB_OUTDOOR_LOCATION_TOPIC = "outdoor_location";

    String PUB_FIXED_LOCATION_TOPIC = "fixed_location";

    String FENCE_TYPE = "1001";

    String TAG_TYPE = "1002";

    String SOS_RECOVERY = "1003";

    String FENCE_STRATEGY_TYPE = "1004";

    String EXTRACT_STATISTIC = "2001";

    String EXTRACT_ALARM = "2002";

    String EXTRACT_TAG_LOCATION_DEPT = "2003";

    String BLE_LOCATION = "3001";

    String NMEA_LOCATION = "3002";

    String FIXED_LOCATION = "3003";

    String BLE_BEACON = "3101";

    static Map<String, byte[]> getHeader(String type) {
        return getHeader(type, null);
    }

    static Map<String, byte[]> getHeader(String type, Map<String, String> extracts) {
        Map<String, byte[]> headersMap = new HashMap<>();
        headersMap.put("type", type.getBytes());
        headersMap.put("time", (System.currentTimeMillis() + "").getBytes());
        headersMap.put("server", "test".getBytes());
        headersMap.put("ip", "127.0.0.1".getBytes());
        if (!CollectionUtils.isEmpty(extracts)) {
            extracts.forEach((key, value) -> {
                headersMap.put(key, value.getBytes());
            });
        }
        return headersMap;
    }

}
