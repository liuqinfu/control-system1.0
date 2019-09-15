package com.aether.common.code;


import com.aether.common.finals.CodeFinals;
import com.aether.common.finals.CodeMsgFinals;

/**
 * @author liuqinfu
 */
public class RspFailResult extends RspResult {

	public RspFailResult() {
		super(CodeFinals.EXCEPTION_CODE, CodeMsgFinals.getValue(CodeFinals.EXCEPTION_CODE), null);
	}

	public RspFailResult(Object data) {
		super(CodeFinals.EXCEPTION_CODE, CodeMsgFinals.getValue(CodeFinals.EXCEPTION_CODE), data);
	}

	public RspFailResult(String code, String message, Object data) {
		super(code, message, data);
	}
	
	public RspFailResult(String code, Object data) {
		super(code, CodeMsgFinals.getValue(code), data);
	}
	
	public RspFailResult(String code) {
		super(code, CodeMsgFinals.getValue(code), null);
	}
	
}
