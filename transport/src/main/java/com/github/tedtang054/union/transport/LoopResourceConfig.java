package com.github.tedtang054.union.transport;

import reactor.netty.resources.LoopResources;

/**
 * @Author: dengJh
 * @Date: 2024/05/06 17:41
 */
public class LoopResourceConfig {

    private static LoopResources loopResources;

    public static synchronized LoopResources loopResources(Integer bossCount, Integer workerCount) {
        bossCount = null == bossCount ? 1 : bossCount;
        workerCount = null == workerCount? Runtime.getRuntime().availableProcessors() : workerCount;
        if (null == loopResources) {
            loopResources = LoopResources
                    .create("reactor-transport", bossCount, workerCount, true);
        }
        return loopResources;
    }

}
