package com.aether.mqttservice.configration;

import com.aether.common.utils.StringUtil;
import com.aether.mqttservice.mqttbusiness.client.MQTTClientReceive;
import com.aether.mqttservice.mqttbusiness.client.MQTTClientSend;
import com.aether.mqttservice.mqttbusiness.threads.queue.MqttSenderQueue;
import com.aether.mqttservice.service.CustomerService;
import com.aether.mqttservice.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfiguration {

    public static String MQTT_HOST;
    public static String serverSubTopic;
    public static String deviceSubTopic;
    public static String suffixTopic;
    public static MQTTClientSend mqttClientSend;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DeviceService deviceService;

    @Value("${mqtt.url}")
    public void setMqttHost(String mqttHost) {
        MQTT_HOST = mqttHost;
    }
    @Value("${mqtt.server_sub_topic}")
    public void setServerSubTopic(String serverSubTopic) {
        MqttConfiguration.serverSubTopic = serverSubTopic;
    }
    @Value("${mqtt.device_sub_topic}")
    public void setDeviceSubTopic(String deviceSubTopic) {
        MqttConfiguration.deviceSubTopic = deviceSubTopic;
    }
    @Value("${mqtt.suffix_topic}")
    public void setSuffixTopic(String suffixTopic) {
        MqttConfiguration.suffixTopic = suffixTopic;
    }

    @Bean
    public MQTTClientSend mqttClientSend(){
        mqttClientSend = new MQTTClientSend(MQTT_HOST, StringUtil.get32GUID());
        MqttSenderQueue.runUpdateData(new MqttSenderQueue());
        return mqttClientSend;
    }

    @Bean
    public MQTTClientReceive mqttClientReceive(){
        MQTTClientReceive mqttClientReceive=new MQTTClientReceive(MQTT_HOST,serverSubTopic,deviceSubTopic,suffixTopic,customerService,deviceService);
        return  mqttClientReceive;
    }



}
