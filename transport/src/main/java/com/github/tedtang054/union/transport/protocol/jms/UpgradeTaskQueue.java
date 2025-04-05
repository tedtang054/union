package com.github.tedtang054.union.transport.protocol.jms;

import com.github.tedtang054.union.common.event.UpgradeStateEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: dengJh
 * @Date: 2024/09/25 10:02
 */
@Slf4j
public class UpgradeTaskQueue {

    private static final ConcurrentHashMap<String, UpgradeItem> PUSHING_DEVICE = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, UpgradeItem> WORKING_JOBS = new ConcurrentHashMap<>();

    ApplicationEventPublisher eventPublisher;

    public UpgradeTaskQueue(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void addJobs(List<UpgradeItem> items) {
        log.debug("receive upgrade event : {}", items);
        items.forEach(item -> {
            item.setAddTime(System.currentTimeMillis());
            PUSHING_DEVICE.putIfAbsent(item.getCode(), item);
        });
    }

    /**
     * 移除升级任务
     * @param code
     * @param itemId
     * @param force 强制终止任务
     * @return
     */
    public boolean deleteJob(String code, Integer itemId, boolean force, Integer activeTimeout) {
        UpgradeItem pushItem = PUSHING_DEVICE.get(code);
        if (null != pushItem && pushItem.getItemId().equals(itemId)) {
            PUSHING_DEVICE.remove(code);
            return true;
        }
        UpgradeItem workingItem = WORKING_JOBS.get(code);
        if (null == workingItem || !workingItem.getItemId().equals(itemId)) {
            return true;
        }
        var expired = null != workingItem.getActiveTime()
                && System.currentTimeMillis() - workingItem.getActiveTime() <= activeTimeout * 1000;
        if (expired && !force) {
            return false;
        }
        workingItem.setActiveTime(null);
        WORKING_JOBS.remove(code);
        return true;
    }


    public UpgradeItem getJob(String code) {
        UpgradeItem upgradeItem = WORKING_JOBS.get(code);
        if (null != upgradeItem) {
            return upgradeItem;
        }
        upgradeItem = PUSHING_DEVICE.remove(code);
        if (null == upgradeItem) {
            return null;
        }
        WORKING_JOBS.put(code, upgradeItem);
        return upgradeItem;
    }

    public void updateSuccess(String codeStr, InetSocketAddress peer) {
        UpgradeItem upgradeItem = WORKING_JOBS.remove(codeStr);
        if (null == upgradeItem) {
            log.error("receive success signal, non exist job, code : {}, client : {}", codeStr, peer);
            return;
        }
        log.info("device upgrade success deviceId : {}, code : {}, itemId : {}, client : {} ",
                upgradeItem.getDeviceId(), upgradeItem.getCode(), upgradeItem.getItemId(), peer);
        eventPublisher.publishEvent(new UpgradeStateEvent(upgradeItem.getTaskId(),
                upgradeItem.getItemId(), upgradeItem.getCode(), (byte) 2));
    }

    public void updateFail(String codeStr, InetSocketAddress peer) {
        UpgradeItem upgradeItem = WORKING_JOBS.get(codeStr);
        if (null == upgradeItem) {
            log.error("receive fail signal, non exist job, code : {}, client : {}", codeStr, peer);
            return;
        }
        log.error("device upgrade fail deviceId : {}, code : {}, itemId : {}, client : {} ",
                upgradeItem.getDeviceId(), upgradeItem.getCode(), upgradeItem.getItemId(), peer);
        eventPublisher.publishEvent(new UpgradeStateEvent(upgradeItem.getTaskId(),
                upgradeItem.getItemId(), upgradeItem.getCode(), (byte) 3));
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

        private Long activeTime;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"taskId\":").append(taskId);
            sb.append(", \"itemId\":").append(itemId);
            sb.append(", \"deviceId\":").append(deviceId);
            sb.append(", \"code\":\"").append(code).append('\"');
            sb.append('}');
            return sb.toString();
        }
    }
}
