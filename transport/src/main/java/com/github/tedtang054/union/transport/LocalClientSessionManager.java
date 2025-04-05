package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.constant.TransportConstants;
import com.github.tedtang054.union.transport.constant.TransportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 10:54
 */
@Slf4j
public class LocalClientSessionManager implements ClientSessionManager {

    private static final Map<InetSocketAddress, PeerEmitter> EMITTERS = new ConcurrentHashMap<>(32);

    private static final Map<InetSocketAddress, ClientSession> SESSIONS = new ConcurrentHashMap<>(32);

    private static final Map<InetSocketAddress, Connection> CONNECTIONS = new ConcurrentHashMap<>(32);

    private static final Map<String, InetSocketAddress> IDENTITY_ADDRESSES = new ConcurrentHashMap<>(32);

    @Override
    public Boolean fireMsg(InetSocketAddress client, List<ServerMessage> messages) {
        try {
            var peerEmitter = EMITTERS.get(client);
            for (ServerMessage message : messages) {
                if (null != message.getEmpty() && message.getEmpty()) {
                    continue;
                }
                if (TransportType.TCP.equals(message.getTransportType())) {
                    peerEmitter.getEmitter().next(message);
                } else {
                    EMITTERS.get(TransportConstants.UDP_SERVER).getEmitter().next(message);
                }
                if (null != message.getLoginFailed() && message.getLoginFailed()) {
                    kickOff(client, "login failed", true).subscribe();
                }
            }
            return true;
        } catch (Exception e) {
            log.error("fire msg error : ", e);
            return false;
        }
    }

    @Override
    public Boolean fireMsg(String identity, List<ServerMessage> messages) {
        var client = IDENTITY_ADDRESSES.get(identity);
        if (null != client) {
            return fireMsg(client, messages);
        }
        return false;
    }

    @Override
    public void registerConnection(Connection connection, Integer timeout) {
        var channel = connection.channel();
        InetSocketAddress client = (InetSocketAddress) channel.remoteAddress();
        connection.onReadIdle(timeout, () -> cleanUp(client, "read idle timeout"));
//        connection.onWriteIdle(timeout, () -> cleanUp(client, "write idle timeout"));
        channel.closeFuture().addListener(future -> {
            kickOff(client, "disconnected", false).subscribe();
        });
        CONNECTIONS.put(client, connection);
    }

    @Override
    public InetSocketAddress getRemoteAddress(String identity) {
        return IDENTITY_ADDRESSES.get(identity);
    }

    @Override
    public Mono<Void> kickOff(InetSocketAddress client, String reason, boolean sendMsg) {
        return sendMsg ?
                Mono.delay(Duration.of(1, ChronoUnit.MILLIS)).doOnNext(l -> cleanUp(client, reason)).then()
                : Mono.just(1).doOnNext(l -> cleanUp(client, reason)).then();
    }

    @Override
    public Mono<Boolean> registerSession(InetSocketAddress client, VerifyMsg msg) {
        var session = ClientSession.builder()
                .identity(msg.getIdentity())
                .transportType(msg.getTransportType())
                .protocol(msg.getProtocol())
                .heartbeatTime(System.currentTimeMillis())
                .loginTime(System.currentTimeMillis())
                .sessionId(ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L))
                .peer(client)
                .seq(1)
                .build();
        SESSIONS.putIfAbsent(client, session);
        IDENTITY_ADDRESSES.putIfAbsent(msg.getIdentity(), client);
        return Mono.just(true);
    }

    @Override
    public Flux<ServerMessage> registerEmitter(InetSocketAddress client) {
        var peerEmitter = EMITTERS.get(client);
        if (null != peerEmitter) {
            return null;
        }
        Flux<ServerMessage> flux = Flux
                .<ServerMessage>create(emitter -> EMITTERS.get(client).setEmitter(emitter))
                .onBackpressureBuffer(20000, BufferOverflowStrategy.DROP_OLDEST)
                .doOnError(e -> log.error("emitter data error : ", e))
                .doOnComplete(() -> log.debug("outbound flux complete peer : {}", client));
        EMITTERS.put(client, new PeerEmitter(flux, null, System.currentTimeMillis()));
        return flux;
    }

    @Override
    public Mono<Boolean> updateHeartbeat(InetSocketAddress client) {
        var session = SESSIONS.get(client);
        if (session == null) {
            return Mono.just(false);
        }
        session.updateHeartBeat();
        return Mono.just(true);
    }

    @Override
    public Mono<ClientSession> getSession(InetSocketAddress client) {
        return Mono.just(SESSIONS.get(client));
    }

    @Override
    public Mono<Boolean> checkLogin(InetSocketAddress client) {
        return Mono.just(null != SESSIONS.get(client));
    }

    private static void cleanUp(InetSocketAddress client, String reason) {
        log.debug("cleanup connection reason : {} remote ip : {}, port : {}",
                reason, client.getHostString(), client.getPort());
        var emitter = EMITTERS.remove(client);
        if (emitter != null) {
            emitter.getEmitter().complete();
        }
        SESSIONS.remove(client);
        var connection = CONNECTIONS.remove(client);
        if (null!= connection) {
            connection.dispose();
        }
        IDENTITY_ADDRESSES.entrySet().stream()
                .filter(entry -> entry.getValue().equals(client))
                .findFirst().ifPresent(entry -> IDENTITY_ADDRESSES.remove(entry.getKey()));
    }
}
@Data
@AllArgsConstructor
class PeerEmitter {

    private Flux<ServerMessage> flux;

    private FluxSink<ServerMessage> emitter;

    private Long activeTime;
}