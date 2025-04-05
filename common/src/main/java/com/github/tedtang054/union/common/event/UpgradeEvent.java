package com.github.tedtang054.union.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: dengJh
 * @Date: 2024/09/23 11:25
 */
@Getter
public class UpgradeEvent extends ApplicationEvent {

    List<UpgradeItem> events = new CopyOnWriteArrayList<>();

    private Byte state;

    public UpgradeEvent() {
        super("upgrade");
    }

    public void addEvent(List<UpgradeItem> items, Byte state) {
        events.addAll(items);
        this.state = state;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpgradeItem {

        // 任务id
        private Integer taskId;
        // 子任务id
        private Integer itemId;

        private Integer deviceId;

        private String code;

        private byte[] firmware;

        private Long addTime;

    }
}
