package com.github.tedtang054.union.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: dengJh
 * @Date: 2024/10/23 14:28
 */
public class UploadEvent extends ApplicationEvent {

    List<UploadDto> events = new CopyOnWriteArrayList<>();

    public UploadEvent() {
        super("rfidUploadEvent");
    }

    public List<UploadDto> getEvents() {
        return events;
    }

    public void addEvent(UploadDto dto) {
        this.events.add(dto);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadDto {

        // 基站id
        private Integer stationId;

        // 数据时间，单位秒
        private Integer dataTime;

        // 标签/手环id
        private Integer cardId;

        // 低电量提示
        private Boolean lowPower;

        // 低频地址码，每个门口设置两个低频地址码，如A门设置门外为03，门内为04，B门门外为05，门内为06，
        // 以此类推，经过哪个门口，上传哪个低频数据了就能知道在哪个门
        private Short addressNum;

        // 功能标记位，单字符，a->定位功能
        private String function;

    }
}
