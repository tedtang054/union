package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:52
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayRequest implements ServerVo {

    /**
     * 语音播报 0：发卡成功，1：发卡失败，2:还卡成功，3:还卡失败
     */
    private Byte index;

}
