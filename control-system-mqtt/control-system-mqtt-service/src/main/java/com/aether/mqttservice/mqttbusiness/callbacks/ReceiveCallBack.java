package com.aether.mqttservice.mqttbusiness.callbacks;


import com.aether.mqttservice.mqttbusiness.threads.mqtt.RecBussiness;
import com.aether.mqttservice.mqttbusiness.threads.queue.MqttSenderQueue;
import com.aether.mqttservice.service.CustomerService;
import com.aether.mqttservice.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

@Slf4j
public class ReceiveCallBack implements MqttCallbackExtended {

    public String serverSubTopic;
    public String deviceSubTopic;
    public String suffixTopic;
    private MqttClient client;

    private CustomerService customerService;
    private DeviceService deviceService;


    public ReceiveCallBack(String serverSubTopic,
                           String deviceSubTopic,
                           String suffixTopic,
                           MqttClient recClientId,CustomerService customerService,DeviceService deviceService) {
        this.serverSubTopic = serverSubTopic;
        this.deviceSubTopic = deviceSubTopic;
        this.suffixTopic = suffixTopic;
        this.client = recClientId;
        this.customerService = customerService;
        this.deviceService = deviceService;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("receive-client lost connection,reconnecting");
    }

    @Override
    public void messageArrived(String fromtopic, MqttMessage mqttMessage) throws Exception {
//        log.info("mqttservice received msg; topic:{}  -----message:{}", fromtopic, new String(mqttMessage.getPayload()));
        log.info("mqttservice received msg; topic:{}", fromtopic);
        try {
            MqttSenderQueue.runUpdateData(new RecBussiness(
                    serverSubTopic,
                    deviceSubTopic,
                    fromtopic,
                    mqttMessage,
                    customerService,deviceService));
        } catch (Exception e) {
            log.error("MQTTClientReceive error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }

    @Override
    public void connectComplete(boolean b, String s) {
        log.info("receive-client connectted");
        String[] topic1 = {serverSubTopic+suffixTopic};
        try {
            client.subscribe(topic1);
            log.info("receive-client reconnected");
        } catch (MqttException e) {
            log.error("receive-client reconnect error:" + e.getMessage());
        }
    }
}
