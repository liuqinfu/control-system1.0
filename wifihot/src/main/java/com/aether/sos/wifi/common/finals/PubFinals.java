package com.aether.sos.wifi.common.finals;

/**
 * @author liuqinfu
 */
public final class PubFinals {

	/** 令牌加密随机数*/
	public final static String AUTH_RANDOMNUMBER = "65s4g54w5g4645g947";
	/** 令牌头字符串*/
	public static final String AUTH_STRING = "AUTH_CODE";
	/** 登陆接口数据体-用户对象key名称*/
	public static final String DATA_USER = "user_data";
	/** 登陆接口数据体-用户对象用户名key名称*/
	public final static String AUTH_KEY_USER ="username";
	/** 登陆接口数据体-用户对象角色key名称*/
	public final static String AUTH_KEY_USER_ROLE = "userRole";
	/** 登陆接口数据体-用户对象iidd key名称*/
	public final static String AUTH_KEY_USER_IIDD = "iidd";

	/* 分页参数配置*/
	public final static String PAGE_DATA="pageData";
	public final static String PAGE_TOTAL="total";
	public final static String PAGE_COUNT="pages";
	public final static String CURRENT_PAGE="currPage";
	public final static String PAGE_NOW="pageNow";
	public final static String PAGE_PREV="pagePre";
	public final static String PAGE_NEXT="pageNext";
	public final static Integer PAGE_SIZE_DEFAULT=10;
	public final static String PAGE_SUFFIX="ToPage";
	public final static String PAGE_SIZE="pageSize";
	public final static String PAGE_INDEX="pageNum";

	/*WiFi探针上报间隔时间，单位为秒*/
	public final static int WIFI_PROBE_INTERVAL_TIME = 5;
	/*WiFi探针预警分析间隔时间，单位为秒*/
	public final static int WIFI_PROBE_ANALYSIS_INTERVAL_TIME = 1 * 60;
	/*WiFi探针警报有效距离，单位为米*/
	public final static int WIFI_PROBE_DISTANCE = 10;
	/*WiFi探针警报检测在线次数*/
	public final static int WIFI_PROBE_QUANTITY = 5;
	/*WiFi探针在线时间比率，单位% */
	public final static int WIFI_PROBE_ONLINE_RATE = 70;

}
