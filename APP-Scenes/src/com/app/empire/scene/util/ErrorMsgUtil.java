package com.app.empire.scene.util;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.error.ErrorMsgProto.ErrorMsg;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.protocol.data.AbstractData.EnumTarget;

public class ErrorMsgUtil {

	/**
	 * 
	 * @param army
	 * @param type 请求的协议号
	 * @param subType 请求的协议号
	 * @param code 错误码
	 * @param msg 消息
	 */
	public static void sendErrorMsg(ArmyProxy army, short type, short subType, int code, String msg) {
		if (army == null)
			return;
		ErrorMsg.Builder errorMsg = ErrorMsg.newBuilder();
		errorMsg.setErrorType(type);
		errorMsg.setErrorSubType(subType);
		errorMsg.setCode(code);
		errorMsg.setMsg(msg);

		// PBMessage pkg = MessageUtil.buildMessage(Protocol.U_RESP_ERROR, resp);
		army.sendPbMessage(Protocol.MAIN_ERROR, Protocol.ERROR_ProtocolError, errorMsg.build(), EnumTarget.CLIENT.getValue());
	}
}
