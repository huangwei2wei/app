package com.app.empire.protocol.data.error;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
/**
 * 类 <code>ProtocolError</code>继承抽象类<code>AbstractData</code>
 * ，实现接口主命令Protocol.MAIN_ERROR下子命令ERROR_ProtocolError(接口错误)对应数据封装。
 * 
 * @see AbstractData
 * @author doter
 */
public class ProtocolError extends AbstractData {
	private byte errorType;// 错误对应协议
	private byte errorSubType;// 错误对应协议
	private int code;// 错误码
	private String msg;// 消息

	public ProtocolError(int sessionId, int serial) {
		super(Protocol.MAIN_ERROR, Protocol.ERROR_ProtocolError, sessionId, serial);
	}

	public ProtocolError() {
		this(Protocol.MAIN_ERROR, Protocol.ERROR_ProtocolError);
	}

	public ProtocolError(ProtocolException ex) {
		this(ex.getSessionId(), ex.getSerial());
		setErrorType(ex.getType());
		setErrorSubType(ex.getSubType());
		setMsg(ex.getMessage());
		if (ex.getMessage() == null)
			setMsg("");
	}

	public byte getErrorType() {
		return errorType;
	}

	public void setErrorType(byte errorType) {
		this.errorType = errorType;
	}

	public byte getErrorSubType() {
		return errorSubType;
	}

	public void setErrorSubType(byte errorSubType) {
		this.errorSubType = errorSubType;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
