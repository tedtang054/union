package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayResponse implements ClientVo {

    /**
     * 	播放位置
     */
    private Byte index;
    /**
     * 	最大语音个数
     */
    private Short max;

}
