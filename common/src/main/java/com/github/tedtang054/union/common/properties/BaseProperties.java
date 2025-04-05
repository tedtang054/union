package com.github.tedtang054.union.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: dengJh
 * @Date: 2025/04/04 9:47
 */
@Data
@ConfigurationProperties("custom.base")
public class BaseProperties {

    private String env;


}
