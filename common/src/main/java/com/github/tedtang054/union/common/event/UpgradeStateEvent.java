package com.github.tedtang054.union.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: dengJh
 * @Date: 2024/09/25 19:12
 */
@Getter
public class UpgradeStateEvent extends ApplicationEvent {

    private Integer taskId;

    private Integer itemId;

    private String code;

    // 2升级完成，3升级失败
    private Byte state;

    public UpgradeStateEvent() {
        super("upgradeState");
    }

    public UpgradeStateEvent(Integer taskId, Integer itemId, String code, Byte state) {
        super("upgradeState");
        this.taskId = taskId;
        this.itemId = itemId;
        this.code = code;
        this.state = state;
    }
}
