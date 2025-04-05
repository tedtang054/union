package com.github.tedtang054.union.transport.constant;

import com.github.tedtang054.union.transport.service.CabinetDataBaseService;
import com.github.tedtang054.union.transport.ProtocolHandler;
import com.github.tedtang054.union.transport.channel.PipelineInitializer;
import com.github.tedtang054.union.transport.protocol.jms.CabinetDefaultCmdSender;
import com.github.tedtang054.union.transport.protocol.jms.CabinetDefaultMsgHandler;
import com.github.tedtang054.union.transport.protocol.jms.CabinetProtocolHandler;
import com.github.tedtang054.union.transport.protocol.jms.UpgradeProtocolHandler;
import com.github.tedtang054.union.transport.protocol.jms.UpgradeTaskQueue;
import com.github.tedtang054.union.transport.protocol.jms.channel.CabinetPipelineInitializer;
import com.github.tedtang054.union.transport.protocol.jms.channel.UpgradePipelineInitializer;
import com.github.tedtang054.union.transport.protocol.rfid.RfidProtocolHandler;
import com.github.tedtang054.union.transport.protocol.rfid.channel.RfidPipelineInitializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/07/03 10:18
 */
@Getter
public enum ClientProtocol {

    FIRMWARE("FIRMWARE", "固件升级",
            new ProtocolClasses(UpgradePipelineInitializer.class, UpgradeProtocolHandler.class, List.of(UpgradeTaskQueue.class))),
    CABINET("CABINET", "充电柜",
            new ProtocolClasses(CabinetPipelineInitializer.class, CabinetProtocolHandler.class,
                    List.of(CabinetDataBaseService.class, CabinetDefaultMsgHandler.class, CabinetDefaultCmdSender.class))),
    RFID_STATION("RFID", "rfid",
            new ProtocolClasses(RfidPipelineInitializer.class, RfidProtocolHandler.class, Collections.emptyList()))
    ;

    private final String protocol;

    private final String desc;

    private final ProtocolClasses protocolClasses;

    ClientProtocol(String protocol, String desc,
                   ProtocolClasses protocolClasses) {
        this.protocol = protocol;
        this.desc = desc;
        this.protocolClasses = protocolClasses;
    }

    @Data
    @AllArgsConstructor
    public static class ProtocolClasses {

        private Class<? extends PipelineInitializer> initializerClass;

        private Class<? extends ProtocolHandler> protocolHandlerClass;

        // 依赖属性类
        private List<Class<?>> dependencyClasses;

    }

}
