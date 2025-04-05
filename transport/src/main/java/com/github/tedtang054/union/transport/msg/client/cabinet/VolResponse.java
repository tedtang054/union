package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VolResponse implements ClientVo {

    /**
     * 音量，最小1，最大100
     */
    private Byte n;

}
