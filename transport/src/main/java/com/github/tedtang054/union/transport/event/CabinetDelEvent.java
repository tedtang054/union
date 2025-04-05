package com.github.tedtang054.union.transport.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/05/13 9:37
 */
@Getter
public class CabinetDelEvent extends ApplicationEvent {

    private List<Integer> cabinets;

    public CabinetDelEvent(List<Integer> cabinets) {
        super("cabinetDelEvent");
        this.cabinets = cabinets;
    }


}
