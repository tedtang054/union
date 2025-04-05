package com.github.tedtang054.union.transport.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 16:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "custom.device.cabinet")
public class CabinetProperties {

    // 开启http访问接口
    private Boolean enableApi = true;

    // 启用随机发卡
    private Boolean randomRent = false;

}
