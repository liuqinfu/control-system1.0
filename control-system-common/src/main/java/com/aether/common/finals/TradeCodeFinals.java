package com.aether.common.finals;

/** mqtt的消息交易码
 * @author liuqinfu
 * @date 2019/9/5 09:22
 */
public class TradeCodeFinals {

    /**
     * 格式：  主题_TRADECODE_交易码描述   exp: SERVERSUBTOPIC_TRADECODE_BIND_DEVICE
     */

    /**
     * 解除设备绑定
     */
    public static final String DEVICESUBTOPIC_TRADECODE_UNBIND_DEVICE = "8016";

    /**
     * 授权设备给他人
     */
    public static final String DEVICESUBTOPIC_TRADECODE_AUTH_DEVICE_TOOTHER = "8011";

    /**
     * 解除授权让人设备
     */
    public static final String DEVICESUBTOPIC_TRADECODE_UNAUTH_DEVICE_TOOTHER = "8013";

    /**
     * 挑战应答
     */
    public static final String SERVERSUBTOPIC_TRADECODE_CHALLANGE_CODE = "8002";

    /**
     * 设备激活
     */
    public static final String SERVERSUBTOPIC_TRADECODE_ACTIVE_DEVICE = "8008";

    /**
     * 设备上报管理员离线解锁密码
     */
    public static final String SERVERSUBTOPIC_TRADECODE_OFFLINE_KEY = "8009";

    /**
     * 设备上报用户操作记录
     */
    public static final String SERVERSUBTOPIC_TRADECODE_REPORT_OPERATION = "8010";

    /**
     * 设备上报设备电量
     */
    public static final String SERVERSUBTOPIC_TRADECODE_REPORT_POWER = "8020";

    /**
     * 设备上报报警状态
     */
    public static final String SERVERSUBTOPIC_TRADECODE_REPORT_POLICE = "8021";

    /**
     * 设备遗言
     */
    public static final String SERVERSUBTOPIC_TRADECODE_BREATH_WILL = "9000";

    /**
     * 设备上报监控状态
     */
    public static final String SERVERSUBTOPIC_TRADECODE_REPORT_STATUS = "9001";

}
