package com.github.tedtang054.union.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: dengJh
 * @Date: 2024/09/22 10:10
 */
@Getter
public class UpgradeJobEvent extends ApplicationEvent {

    private Boolean start;

    List<FirmwareUpgradeTask> events = new CopyOnWriteArrayList<>();

    public UpgradeJobEvent(Boolean start) {
        super("upgradeJob");
        this.start = start;
    }

    public void addEvent(List<FirmwareUpgradeTask> tasks) {
        events.addAll(tasks);
    }

    @Data
    public static class FirmwareUpgradeTask {

        // 固件升级任务id
        private Integer id;

        // 固件详情
        private FirmwareDetail firmware;

        // 待升级设备
        private List<FirmwareTaskItem> items;

        // 任务超时时间，单位小时
        private Integer timeout;

    }

    @Data
    public static class FirmwareTaskItem {

        // 固件升级子任务id
        private Integer id;

        // 设备id
        private Integer deviceId;

        // 设备号
        private String code;

        // 设备状态，0在线，1离线
        private Byte state;
    }

    @Data
    @AllArgsConstructor
    public static class FirmwareDetail {

        private Integer id;

        private String version;

        private byte[] data;

    }

}
