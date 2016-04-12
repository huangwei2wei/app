package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 类 <code>LoginOk</code>继承抽象类<code>AbstractData</code>
 * ，实现接口主命令Protocol.MAIN_ACCOUNT下子命令ACCOUNT_LoginOk(账户登录成功)对应数据封装。
 * 
 * @see AbstractData
 * @author mazheng
 */
public class GetRandomName extends AbstractData {
	// private int sex;
	public GetRandomName(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRandomName, sessionId, serial);
	}
	public GetRandomName() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRandomName);
	}
	// public int getSex() {
	// return sex;
	// }
	//
	// public void setSex(int sex) {
	// this.sex = sex;
	// }
}
