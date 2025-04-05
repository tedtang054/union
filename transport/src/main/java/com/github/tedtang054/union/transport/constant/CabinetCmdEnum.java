package com.github.tedtang054.union.transport.constant;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 15:04
 */
@Getter
public enum CabinetCmdEnum {

    LOGIN("login"),
    HEART("heart"),
    DETAIL("detail"),
    DETAIL_UP("detailup"),
    RENT("rent"),
    RETURN("return"),
    FORCE("force"),
    UPDATE("updata"),
    REBOOT("reboot"),
    VOL("vol"),
    LIST_FLASH("list_flash"),
    DISPLAY("display"),
    ;

    private final String cmd;

    private static final Map<String, CabinetCmdEnum> CMD_MAP = new HashMap<>();

    static {
        for (CabinetCmdEnum cmd : CabinetCmdEnum.values()) {
            CMD_MAP.put(cmd.cmd, cmd);
        }
    }

    CabinetCmdEnum(String cmd) {
        this.cmd = cmd;
    }

    public static CabinetCmdEnum toCmdEnum(String cmd) {
        return CMD_MAP.get(cmd.toLowerCase());
    }

}
