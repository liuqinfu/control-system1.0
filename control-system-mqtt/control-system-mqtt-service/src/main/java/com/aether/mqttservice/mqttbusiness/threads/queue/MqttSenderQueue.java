package com.aether.mqttservice.mqttbusiness.threads.queue;

import com.aether.mqttservice.configration.MqttConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * mqtt消息发送队列
 * @author liuqinfu
 */
@Slf4j
public class MqttSenderQueue implements Runnable {

    public static final BlockingDeque<Map<String,Object>>msgQueue = new LinkedBlockingDeque<>();
    //公共线程池
    protected static final ExecutorService workerThreadPool = Executors.newFixedThreadPool(8);

    /**
     * 运行Runnable更改数据
     */
    public static void runUpdateData(final Runnable run) {
        workerThreadPool.execute(run);
    }

    @Override
    public void run() {
        log.info("MqttSenderQueue inited and start to work");
        Map<String,Object> msgInfo;
        while (true){
            if (null != (msgInfo =msgQueue.poll())){
                Map<String,Object> temp = msgInfo;
                workerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        String topic = (String) temp.get("topic");
                        MqttMessage msg = (MqttMessage)temp.get("mqttMsg");
                        try {
                            MqttConfiguration.mqttClientSend.publish(topic, msg);
                            log.info("消息发送成功\ttopic:{}",topic);
                        } catch (MqttException e) {
                            log.error("消息发送失败,稍后重新发送\ttopic:{}",topic);
                            msgQueue.offer(temp);
                        }
                    }
                });
            }else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
