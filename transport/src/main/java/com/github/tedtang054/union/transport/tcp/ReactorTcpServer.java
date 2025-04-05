package com.github.tedtang054.union.transport.tcp;

import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.LoopResourceConfig;
import com.github.tedtang054.union.transport.TransportProperties;
import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 14:25
 */
@Slf4j
public class ReactorTcpServer implements ApplicationRunner {

    private TransportProperties properties;

    private DisposableServer disposableServer;

    private TcpHandlerAdapter tcpHandlerAdapter;

    private CompositeDecodeHandler decoderHandler;

    private ClientSessionManager sessionManager;

    public ReactorTcpServer(TransportProperties properties, TcpHandlerAdapter tcpHandlerAdapter,
                            ClientSessionManager sessionManager, CompositeDecodeHandler decoderHandler) {
        this.properties = properties;
        this.tcpHandlerAdapter = tcpHandlerAdapter;
        this.sessionManager = sessionManager;
        this.decoderHandler = decoderHandler;
    }

    public void start() {
        TransportProperties.Tcp tcp = properties.getTcp();
        TcpServer tcpServer = TcpServer.create()
                .doOnChannelInit((observer, channel, remoteAddress) -> {
                    channel.pipeline().addFirst(decoderHandler);
                })
                .doOnConnection(connection -> {
                    sessionManager.registerConnection(connection, tcp.getTimeout());
                })
                .metrics(true)
                .runOn(LoopResourceConfig.loopResources(properties.getBossCount(), properties.getWorkerCount()), true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.AUTO_READ, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .bindAddress(() -> new InetSocketAddress(tcp.getPort()))
                .handle(tcpHandlerAdapter);
        tcpServer.warmup().block();
        disposableServer = tcpServer.bindNow();
        Thread awaitThread = new Thread("tcpServer") {
            @Override
            public void run() {
                disposableServer.onDispose().block();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
        log.info("tcpServer running ........ port : {}", tcp.getPort());
    }

    public void stop() {
        if (null != disposableServer) {
            disposableServer.disposeNow(Duration.ofSeconds(5));
            this.disposableServer = null;
        }
        log.info("tcpServer stopped ........ port : {}", properties.getTcp().getPort());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }
}
