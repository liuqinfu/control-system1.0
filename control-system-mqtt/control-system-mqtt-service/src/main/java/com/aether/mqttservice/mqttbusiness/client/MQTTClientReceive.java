package com.aether.mqttservice.mqttbusiness.client;

import com.aether.common.utils.StringUtil;
import com.aether.mqttservice.mqttbusiness.callbacks.ReceiveCallBack;
import com.aether.mqttservice.service.CustomerService;
import com.aether.mqttservice.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * 模拟一个客户端接收消息
 *
 * @author rao
 */
@Slf4j
public class MQTTClientReceive {

    public String HOST;
    public String serverSubTopic;
    public String deviceSubTopic;
    public String suffixTopic;
    private String clientid = StringUtil.get32GUID();

    private MqttClient client;
    private MqttConnectOptions options;


    public MQTTClientReceive(String host,
                             String serverSubTopic,
                             String deviceSubTopic,
                             String suffixTopic,
                             CustomerService customerService,
                             DeviceService deviceService) {
        try {
            this.HOST = host;
            this.serverSubTopic = serverSubTopic;
            this.deviceSubTopic = deviceSubTopic;
            this.suffixTopic = suffixTopic;
            log.info("MQTTClientReceive instanced");
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(20);
            // 设置会话心跳时间 单位为秒 服务器会每隔5秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(10);
            options.setAutomaticReconnect(true);//设置自动重连
            // 设置回调
            client.setCallback(new ReceiveCallBack(
                    serverSubTopic,
                    deviceSubTopic,
                    suffixTopic,
                    client,
                    customerService,deviceService));
            client.connect(options);
            //订阅消息
            String[] topic1 = {serverSubTopic+suffixTopic};
            client.subscribe(topic1);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}