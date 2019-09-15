package com.aether.common.listener;

import com.aether.common.utils.SendSMS;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 发送验证码队列
 * @author liuqinfu
 */
@Slf4j
public class CaptachaQueue implements Runnable {

    public static final BlockingDeque<String> captachaQueue = new LinkedBlockingDeque<>();
    //公共线程池
    protected static final ExecutorService workerThreadPool = Executors.newFixedThreadPool(8);
    private String location;

    public CaptachaQueue(String location) {
        this.location = location;
    }

    /**
     * 运行Runnable更改数据
     */
    public static void runUpdateData(final Runnable run) {
        workerThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    run.run();
                } catch (Exception e) {
                    log.error("[runAndLog]", e);
                }
            }
        });
    }

    @Override
    public void run() {
        log.info("CaptachaQueue inited and start to work");
        String codeEntityStr;
        while (true){
            if (!StringUtils.isEmpty(codeEntityStr =captachaQueue.poll())){
                String finalCodeEntityStr = codeEntityStr;
                workerThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = JSONObject.parseObject(finalCodeEntityStr);
                        String target = (String) jsonObject.get("target");
                        Integer targetType =  (Integer) jsonObject.get("targetType");
                        String countryCode = (String) jsonObject.get("countryCode");
                        String content = (String) jsonObject.get("content");
                        String content_en = (String) jsonObject.get("content_en");
                        if (targetType ==1){
                            //发送短信
                            if ("+86".equals(countryCode) && "CN".equals(location)){
                                log.info("国内短信通道");
                                if (!SendSMS.getInstance().sendCodeCN(target, content)) {
                                }
                            }else{
                                log.info("国际短信通道");
                                if (!SendSMS.getInstance().sendCodeForeign(countryCode.substring(1)+target, content_en)) {
                                }
                            }

                        }else {
                            //发送邮件
                            if (!SendSMS.getInstance().sendMailCode(target, content+"\r\n"+content_en)) {
                            }
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
