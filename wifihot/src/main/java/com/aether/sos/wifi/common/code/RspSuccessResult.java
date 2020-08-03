package com.aether.sos.wifi.common.code;


import com.aether.sos.wifi.common.finals.CodeFinals;
import com.aether.sos.wifi.common.finals.CodeMsgFinals;

/**
 * 接口调用成功返回
 * @author Administrator
 *
 */
public class RspSuccessResult extends RspResult {

	public RspSuccessResult() {
		super(CodeFinals.SUCESS_CODE, CodeMsgFinals.getValue(CodeFinals.SUCESS_CODE), null);
	}

	public RspSuccessResult(Object data) {
		super(CodeFinals.SUCESS_CODE, CodeMsgFinals.getValue(CodeFinals.SUCESS_CODE), data);
	}

	public RspSuccessResult(String code, String message, Object data) {
		super(code, message, data);
	}

}
