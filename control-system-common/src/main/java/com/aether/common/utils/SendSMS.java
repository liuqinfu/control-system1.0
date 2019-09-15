package com.aether.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * @author liuqinfu
 */
@Slf4j
public class SendSMS {

    private static SendSMS sendSMS = new SendSMS();

    private String url_CN;
    private String username_CN;
    private String password_CN;
    private String productid_CN;

    private String url_EN;
    private String username_EN;
    private String password_EN;
    private String productid_EN;

    private String fromMail;
    private String mailPwd;
    private String mailHost;

    /**
     * @param args
     */
    /*public static void main(String[] args) throws InterruptedException {
//		String url ="http://www.ztsms.cn/sendNSms.do";
//		String username ="xingyu888";//内容
//		String password ="Xingyu201688";//密码
//		String mobile ="";//号码
//		String content ="注册验证码：123456";//内容
//		String productid ="676767";//产品id
//		String xh ="";//没有
//		String tkey =TimeUtil.getNowTime("yyyyMMddHHmmss");
//		try{
//			content=URLEncoder.encode(content,"utf-8");
//		}catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String param="url="+url+"&username="+username+"&password="+MD5Gen.getMD5(MD5Gen.getMD5(password)+tkey)+"&tkey="+tkey+"&mobile="+mobile+"&content="+content+"&productid="+productid+"&xh"+xh;
//		String ret=HttpRequest.sendPost(url, param);//sendPost or sendGet  即get和post方式
//		System.out.println("ret:"+ret);
//		System.out.println(param);

//        sendCodeCN("13632666226", "123456789");
    }*/

    private SendSMS(){}

    public static SendSMS getInstance(){
        return sendSMS;
    }

    public static void initSendSMS(String url_CN, String username_CN, String password_CN, String productid_CN, String url_EN, String username_EN, String password_EN, String productid_EN, String fromMail, String mailPwd, String mailHost) {
        sendSMS.url_CN = url_CN;
        sendSMS.username_CN = username_CN;
        sendSMS.password_CN = password_CN;
        sendSMS.productid_CN = productid_CN;
        sendSMS.url_EN = url_EN;
        sendSMS.username_EN = username_EN;
        sendSMS.password_EN = password_EN;
        sendSMS.productid_EN = productid_EN;
        sendSMS.fromMail = fromMail;
        sendSMS.mailPwd = mailPwd;
        sendSMS.mailHost = mailHost;
    }

    //提取手机号码生成的验证码,参数：phoneNumber 手机号码， code 验证码
    /**
     * 大陆发送短信
     *
     * @param phoneNumber 收信手机号
     * @param contents    短信内容
     * @return
     */
    public boolean sendCodeCN(String phoneNumber, String contents) {
        String mobile = phoneNumber;//号码
        String content = contents;//内容
        String xh = "";//没有
        String tkey = TimeUtil.getNowTime("yyyyMMddHHmmss");
        try {
            content = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "url=" + url_CN + "&username=" + username_CN + "&password=" + MD5Gen.getMD5(MD5Gen.getMD5(password_CN) + tkey) + "&tkey=" + tkey + "&mobile=" + mobile + "&content=" + content + "&productid=" + productid_CN + "&xh" + xh;
        log.info("request sms_service   request----->url:" + url_CN + "\r\nparam:" + param);
        String ret = HttpRequest.sendPost(url_CN, param);//sendPost or sendGet  即get和post方式
        log.info("request sms_service   response:<-----:" + ret);
        String[] split = ret.split(",");
        if (split.length > 1 && "1".equals(split[0])) {
            //执行成功了，短信已经成功的发送到了用户的手机了
            return true;
        }
        return false;

    }

    /**
     * 国际发送短信
     *
     * @param phoneNumber 收信手机号
     * @param contents    短信内容
     * @return
     */
    public boolean sendCodeForeign(String phoneNumber, String contents) {
        String mobile = phoneNumber;//号码
        String content = contents;//内容
        try {
            content = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "url=" + url_EN + "&username=" + username_EN + "&password=" + MD5Gen.getMD5(password_EN) + "&mobile=" + mobile + "&content=" + content + "&productid=" + productid_EN;
        log.info("request sms_service   request----->url:" + url_EN + "\r\nparam:" + param);
        String ret = HttpRequest.sendPost(url_EN, param);//sendPost or sendGet  即get和post方式
        log.info("request sms_service   response:<-----:" + ret);
        String[] split = ret.split(",");
        if (split.length > 1 && "1".equals(split[0])) {
            //执行成功了，短信已经成功的发送到了用户的手机了
            return true;
        }
        return false;

    }

    /**
     * 发送邮件
     * @param toMail
     * @param contents
     * @return
     */
    public boolean sendMailCode(String toMail, String contents) {
        try {
            Properties prop = new Properties();
            prop.setProperty("mail.host", mailHost);
            prop.setProperty("mail.transport.protocol", "smtp");
            prop.setProperty("mail.smtp.auth", "true");
            //使用JavaMail发送邮件的5个步骤
            //1、创建session
            Session session = Session.getInstance(prop);
            //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            session.setDebug(false);
            //2、通过session得到transport对象
            Transport ts = session.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            ts.connect(mailHost, fromMail, mailPwd);
            //4、创建邮件
            Message message = createSimpleMail(session,fromMail,toMail,contents);
            //5、发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
            //执行成功了，邮件已经成功的发送到了用户的邮箱了
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 创建一个只包含文本的邮件
     *
     * @param session
     * @param from
     * @param to
     * @param textContents
     * @return
     * @throws Exception
     */
    public static MimeMessage createSimpleMail(Session session, String from, String to, String textContents)
            throws Exception {
        //创建邮件对象
        MimeMessage message = new MimeMessage(session);
        //指明邮件的发件人
        message.setFrom(new InternetAddress(from));
        //指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        //邮件的标题
        message.setSubject("永达智能安全密码锁验证码");
        //邮件的文本内容
        message.setContent(textContents, "text/html;charset=UTF-8");
        //返回创建好的邮件对象
        return message;
    }
}
