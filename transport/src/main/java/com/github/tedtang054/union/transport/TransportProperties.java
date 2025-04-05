package com.github.tedtang054.union.transport;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/07/03 10:35
 */
@Data
@ConfigurationProperties(prefix = "custom.transport")
public class TransportProperties {

    private Integer bossCount;

    private Integer workerCount;

    private Tcp tcp;

    private Udp udp;

    @Data
    public static class Tcp extends Transport {

        @Override
        public Integer getPort() {
            return null == super.getPort() ? 7788 : super.getPort();
        }
    }

    @Data
    public static class Udp extends Transport {

        @Override
        public Integer getPort() {
            return null == super.getPort() ? 7788 : super.getPort();
        }
    }

    @Data
    public static class Transport {

        // 是否开启
        private Boolean enable = false;

        // 监听端口
        private Integer port = 7788;

        // 超时时间，单位毫秒
        private Integer timeout = 65000;

        // 启用协议
        private List<String> protocols;

    }

}
