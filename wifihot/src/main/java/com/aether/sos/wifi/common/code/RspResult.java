package com.aether.sos.wifi.common.code;

/**
 * 定义接口调用结果返回主体结构
 * @author Administrator
 *
 */
public class RspResult {
	
	/* 返回结果状态码*/
	private String code;
	/* 返回结果描述*/
	private String message;
	/* 返回结果数据体*/
	private Object data;
	
	public RspResult() {
		super();
	}
	
	public RspResult(String code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	/**
	 * 返回结果状态码
	 * @return
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 返回结果状态码
	 * @return
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 返回结果描述
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * 返回结果描述
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * 返回结果数据体
	 * @return
	 */
	public Object getData() {
		return data;
	}
	/**
	 * 返回结果数据体
	 * @return
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
}
