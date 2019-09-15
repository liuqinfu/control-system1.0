package com.aether.mqttservice.mqttbusiness.client;

import com.aether.mqttservice.mqttbusiness.callbacks.SendCallback;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Title:Server 这是发送消息的服务端
 * Description: 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 * @author rao
 */
@Slf4j
public class MQTTClientSend {

    //tcp://MQTT安装的服务器地址:MQTT定义的端口号
    public String HOST /*= "tcp://202.170.128.108:31882"*/;
    //定义MQTT的ID，可以在MQTT服务配置中指定
    private String clientid ;

    private static MqttClient client;

    private static MqttTopic mqttTopic;

    /**
     * 构造函数
     * @throws MqttException
     */
    public MQTTClientSend(String host, String serverId){
        log.info("MQTTClientSend instance:"+serverId);
        HOST = host;
        clientid = serverId;
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        try {
            if (client == null){
                client = new MqttClient(HOST, clientid, new MemoryPersistence());
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
        connect();
    }

    /**
     *  用来连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        // 设置超时时间
        options.setConnectionTimeout(20);
        // 设置会话心跳时间
        options.setKeepAliveInterval(10);
        options.setAutomaticReconnect(true);//设置自动重连
        try {
          client.setCallback(new SendCallback(client,options,HOST,clientid));
          client.connect(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void publish(String topic,MqttMessage mqttMessage )throws  MqttException {
//        log.info("publish   topic: {} msg:{} ",topic,new String(mqttMessage.getPayload()));
        mqttTopic = client.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
        log.info("##################发送消息完毕");
        log.info("message is published completely! "
                + token.isComplete());
        log.info("messageId:" + token.getMessageId());
        token.getResponse();
        /*if (client.isConnected())
            client.disconnect(10000);*/
        log.info("Disconnected: delivery token \"" + token.hashCode()
                + "\" received: " + token.isComplete());
    }

    //发送消息并获取回执
    public static  void publish(String topic,String msg,int qos,boolean isRetained) throws  MqttException {
        log.info("publish   topic: {} msg:{} ",topic,msg);
        MqttMessage message = new MqttMessage();
        message.setPayload(msg.getBytes());
        message.setQos(qos);
        message.setRetained(isRetained);
        mqttTopic = client.getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(message);
        token.waitForCompletion();
        log.info("##################发送消息完毕");
        log.info("message is published completely! "
                + token.isComplete());
        log.info("messageId:" + token.getMessageId());
        token.getResponse();
        if (client.isConnected()) {
            client.disconnect(10000);
        }
        log.info("Disconnected: delivery token \"" + token.hashCode()
                + "\" received: " + token.isComplete());
    }

}