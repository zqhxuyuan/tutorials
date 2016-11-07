package com.zqh.hadoop.nimbus.utils;

import java.util.HashMap;

public class NimbusException extends Exception {

	private static final long serialVersionUID = 7787822241735490666L;
	private static final HashMap<ExceptionType, String> typeToMessage = new HashMap<ExceptionType, String>();

	private String extraMessage = null;
	private ExceptionType type = null;

	static {
		typeToMessage.put(ExceptionType.FAILED_TO_ACCEPT_CONNECTION,
				"Failed to accept a connection");
		typeToMessage.put(ExceptionType.FAILED_TO_CONNECT,
				"Failed to connect to Nimbus server");
		typeToMessage.put(ExceptionType.FAILED_TO_START_SERVER,
				"Failed to start Nimbus Server.");
		typeToMessage.put(ExceptionType.PROTOCOL_ERROR,
				"Protocol related error.  Not well-formed.");
		typeToMessage.put(ExceptionType.UNKNOWN_SERVER_TYPE,
				"Specified server type is unknown");
	}

	public NimbusException(ExceptionType type) {
		this(type, null);
	}

	public NimbusException(ExceptionType type, String extraMessage) {
		this.extraMessage = extraMessage;
	}

	@Override
	public String getMessage() {
		String msg = typeToMessage.get(type);
		if (msg != null) {
			return msg + (extraMessage != null ? "\t" + extraMessage : "");
		} else {
			return "Unknown error"
					+ (extraMessage != null ? "\t" + extraMessage : "");
		}
	}

	public ExceptionType getType() {
		return type;
	}
}
