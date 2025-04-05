package com.github.tedtang054.union.transport.msg.server.cabinet;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 10:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartResponse implements ServerVo {

    /**
     * 心跳间隔，单位秒
     */
    @JSONField(name = "int")
    private Integer heart;

}
