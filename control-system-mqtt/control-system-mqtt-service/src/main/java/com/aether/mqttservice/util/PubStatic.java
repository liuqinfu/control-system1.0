package com.aether.mqttservice.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liuqinfu
 * @date 2019-06-10 17:59
 */
public class PubStatic {
    //服务端交易码
    public static AtomicLong SERVERSERIALNUM = new AtomicLong(0);

}
