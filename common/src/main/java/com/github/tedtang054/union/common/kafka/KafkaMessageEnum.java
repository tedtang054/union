package com.github.tedtang054.union.common.kafka;

import lombok.Getter;

/**
 * @Author: dengJh
 * @Date: 2022/04/25 10:04
 */
@Getter
public enum KafkaMessageEnum {

    BLUETOOTH_LOCATION(406, "bluetoothLocation", "蓝牙定位"),
    MQTT_LOGIN(409, "mqttLogin", "mqtt设备上线"),
    MQTT_LOGOUT(410, "mqttLogout", "mqtt设备下线"),
    GGA_LOCATION(402, "ggaLocation", "gga定位数据"),
    TAG_LOCATION(500, "tagLocation", "uwb定位数据"),
    TAG_STATUS(501, "tagStatus", "uwb标签状态"),
    TAG_LOGIN(508, "tagLogin", "uwb标签上线"),
    TAG_LOGOUT(509, "tagLogout", "uwb标签下线"),
    STATION_LOGIN(506, "tagLogin", "uwb基站上线"),
    STATION_LOGOUT(507, "tagLogout", "uwb基站下线"),
    JT808_LOCATION(404, "jt808location", "工卡定位"),
    JT808_LOGIN(407, "jt808login", "工卡登录"),
    JT808_LOGOUT(408, "jt808logout", "工卡登出"),
    RFID_LOCATION(420, "rfidLocation", "rfid定位"),
    MERGE_LOCATION(421, "mergeLocation", "融合定位"),
    DA_XIE_HEARTBEAT(403,"daxieHeartbeat", "大榭心跳"),
    ;

    private Integer type;
    private String key;
    private String desc;

    KafkaMessageEnum(Integer type, String key, String desc) {
        this.type = type;
        this.key = key;
        this.desc = desc;
    }
}
