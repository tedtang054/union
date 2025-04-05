package com.github.tedtang054.union.transport.udp;

import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.LoopResourceConfig;
import com.github.tedtang054.union.transport.TransportProperties;
import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * @Author: dengJh
 * @Date: 2023/10/10 8:31
 */
@Slf4j
public class ReactorUdpServer implements ApplicationRunner {

    private TransportProperties properties;

    private UdpHandlerAdapter udpHandlerAdapter;

    private Connection disposableServer;

    private ClientSessionManager sessionManager;

    private CompositeDecodeHandler decoderHandler;

    public ReactorUdpServer(TransportProperties properties, UdpHandlerAdapter udpHandlerAdapter,
                            ClientSessionManager sessionManager, CompositeDecodeHandler decoderHandler) {
        this.properties = properties;
        this.udpHandlerAdapter = udpHandlerAdapter;
        this.decoderHandler = decoderHandler;
        this.sessionManager = sessionManager;
    }

    public void start() {
        TransportProperties.Udp udp = properties.getUdp();
        UdpServer udpServer = UdpServer.create()
                .doOnChannelInit((observer, channel, remoteAddress) -> {
                    channel.pipeline().addFirst(CompositeDecodeHandler.NAME, decoderHandler);
                })
                .runOn(LoopResourceConfig.loopResources(properties.getBossCount(), properties.getWorkerCount()), true)
                .option(ChannelOption.SO_BROADCAST, true)
                .bindAddress(() -> new InetSocketAddress(udp.getPort()))
                .handle(udpHandlerAdapter);
        udpServer.warmup().block();
        disposableServer = udpServer.bindNow();
        Thread awaitThread = new Thread("UdpServer") {
            @Override
            public void run() {
                disposableServer.onDispose().block();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
        log.info("udpServer running ........ port : {}", udp.getPort());
    }

    public void stop() {
        if (null != disposableServer) {
            disposableServer.disposeNow(Duration.ofSeconds(5));
            this.disposableServer = null;
        }
        log.info("udpServer stopped ........ port : {}", properties.getUdp().getPort());
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }
}
