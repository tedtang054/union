package com.github.tedtang054.union.transport.service;

import cn.hutool.core.collection.ListUtil;
import com.github.tedtang054.union.transport.msg.client.cabinet.DetailResponse;
import com.github.tedtang054.union.transport.properties.CabinetProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: dengJh
 * @Date: 2024/04/22 10:04
 */
@Slf4j
public class CabinetDataBaseService {

    CabinetProperties properties;

    private static final Map<String, List<CabinetCard>> STOCKS = new ConcurrentHashMap<>();

    public CabinetDataBaseService(CabinetProperties properties) {
        this.properties = properties;
    }

    public Mono<Boolean> login(String sn, String user, String password) {
        return Mono.just(true);
    }

    public Mono<Boolean> updateDetail(String sn, Byte aims, DetailResponse detail) {
        var targetCabinet = STOCKS.get(sn);
        if (null == targetCabinet) {
            targetCabinet = new ArrayList<>();
            targetCabinet.add(new CabinetCard(detail.getSt(), aims, detail.getD()));
            STOCKS.put(sn, targetCabinet);
            return Mono.just(true);
        }
        var cardMap = targetCabinet.stream()
                .collect(Collectors.toMap(CabinetCard::getAims, d -> d));

        var cards = cardMap.get(aims);
        if (null == cards) {
            targetCabinet.add(new CabinetCard(detail.getSt(), aims, detail.getD()));
            return Mono.just(true);
        }
        cards.setSt(detail.getSt());
        cards.setDetails(detail.getD());
        return Mono.just(true);
    }


    @Data
    @Builder
    @NoArgsConstructor
    public static class CabinetCard {

        private Short st;

        private Byte aims;

        private List<DetailResponse.Detail> details;

        public CabinetCard(Short st, Byte aims, List<DetailResponse.Detail> details) {
            this.st = st;
            this.aims = aims;
            this.details = ListUtil.sort(details, (a, b) -> b.getE() - a.getE());
        }

        public void setDetails(List<DetailResponse.Detail> details) {
            this.details = ListUtil.sort(details, (a, b) -> b.getE() - a.getE());
        }

        public DetailResponse.Detail rentCard() {
            return details.get(0);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Card {

        private Byte aims;

        private DetailResponse.Detail detail;

    }
}
