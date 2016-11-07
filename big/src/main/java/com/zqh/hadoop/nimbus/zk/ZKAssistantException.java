package com.zqh.hadoop.nimbus.zk;

public class ZKAssistantException extends RuntimeException {

	private static final long serialVersionUID = -6622677076486957557L;
	private String msg = null;

	public ZKAssistantException() {
		msg = "ZKAssistantException: not message given";
	}

	public ZKAssistantException(String msg) {
		msg = "ZKAssistantException: " + msg;
	}

	public ZKAssistantException(Exception e) {
		msg = "ZKAssistantException: " + e.getMessage();
	}

	@Override
	public String getMessage() {
		return msg;
	}
}
