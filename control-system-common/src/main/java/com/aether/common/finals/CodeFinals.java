package com.aether.common.finals;


import com.aether.common.annotation.CodeAnnotation;

/**
 * 返回码定义
 * @author Administrator
 *
 */
public final class CodeFinals {
	
	@CodeAnnotation(code="200", msg="操作成功")
	public static final String SUCESS_CODE = "200";
	
	@CodeAnnotation(code="400", msg="令牌已过期")
	public static final String AUTH_FAIL_CODE = "400";
	
	@CodeAnnotation(code="401", msg="非法请求")
	public static final String AUTH_ILLEAGAL_REQ = "401";
	
	@CodeAnnotation(code="402", msg="输入数据检查不通过")
	public static final String DATA_VALID_FAIL = "402";
	
	@CodeAnnotation(code="500", msg="后台程序异常")
	public static final String EXCEPTION_CODE = "500";
	
	@CodeAnnotation(code="10001", msg="用户名或密码不正确")
	public static final String USER_NAME_PWD_FAIL = "10001";

	@CodeAnnotation(code="10002", msg="密码错误")
	public static final String NO_ORIGN_USER = "10002";

	@CodeAnnotation(code="10003", msg="验证码错误或已过期")
	public static final String CHECK_CODE_ERROR = "10003";

	@CodeAnnotation(code="10004", msg="验证码发送失败")
	public static final String SEND_CODE_ERROR = "10004";

	@CodeAnnotation(code="10005", msg="手机号格式错误")
	public static final String CHECK_MOBILE_ERROR = "10005";

	@CodeAnnotation(code="10006", msg="手机号或邮箱已被注册")
	public static final String REGIST_REPEAT_ERROR = "10006";

	@CodeAnnotation(code="10007", msg="账号已在另一台设备登陆，请重新登陆")
	public static final String LOGIN_REPEAT_ERROR = "10007";

	@CodeAnnotation(code="10008", msg="邮箱格式错误")
	public static final String CHECK_MAIL_ERROR = "10008";

	@CodeAnnotation(code="10009", msg="新设备imei登陆需要验证")
	public static final String CHECK_IMEI_ERROR = "10009";

	@CodeAnnotation(code="100010", msg="验证码已过期")
	public static final String CHECK_CODE_EXPIRED = "100010";

	@CodeAnnotation(code="100011", msg="账号不存在")
	public static final String ACCOUNT_NOT_EXIST = "100011";

	@CodeAnnotation(code="100012", msg="设备sn无效")
	public static final String DEVICESN_NOT_EXIST = "100012";

	@CodeAnnotation(code="100013", msg="设备已被他人绑定")
	public static final String DEVICE_HAD_BINDED_OTHER = "100013";

	@CodeAnnotation(code="100014", msg="设备已被自己绑定")
	public static final String DEVICE_HAD_BINDED_MINE = "100014";

	@CodeAnnotation(code="100015", msg="消息id无效")
	public static final String SERIALNUM_IS_VALID = "100015";

	@CodeAnnotation(code="100016", msg="消息未被设备处理")
	public static final String SERIALNUM_NOTDEALED_DEVICE = "100016";

	@CodeAnnotation(code="100017", msg="不是本人设备")
	public static final String DEVICE_NOT_BELONG = "100017";

	@CodeAnnotation(code="100018", msg="设备未被绑定")
	public static final String DEVICE_NOT_BINDED = "100018";

	@CodeAnnotation(code="100019", msg="设备解绑失败")
	public static final String DEVICE_UNBIND_FAIL = "100019";

	@CodeAnnotation(code="100020", msg="设备未激活")
	public static final String DEVICE_UNACTIVE = "100020";


	@CodeAnnotation(code="100021", msg="不能授权自己")
	public static final String DEVICE_CANT_AUTH_TOSELF = "100021";

	@CodeAnnotation(code="100022", msg="设备激活失败(没有更新到记录，并不是出现异常)")
	public static final String DEVICE_ACTIVE_FAIL = "100022";

	@CodeAnnotation(code="100023", msg="管理员密码更新失败(没有更新到记录，并不是出现异常)")
	public static final String DEVICE_ADMINPWD_UPDATE_FAIL = "100023";

}
