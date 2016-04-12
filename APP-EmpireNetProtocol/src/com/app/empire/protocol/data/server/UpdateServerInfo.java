package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 更新服务器信息
 * 
 * @see AbstractData
 * @author doter
 */
public class UpdateServerInfo extends AbstractData {
	private String area;
	private int machineId;
	private int line;
	private String version;
	private String updateurl;
	private String remark;
	private String appraisal;
	private String group;
	private String address;

	public UpdateServerInfo(int sessionId, int serial) {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_UpdateServerInfo, sessionId, serial);
	}

	public UpdateServerInfo() {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_UpdateServerInfo);
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getMachineId() {
		return machineId;
	}

	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUpdateurl() {
		return updateurl;
	}

	public void setUpdateurl(String updateurl) {
		this.updateurl = updateurl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAppraisal() {
		return appraisal;
	}

	public void setAppraisal(String appraisal) {
		this.appraisal = appraisal;
	}

	public String getGroup() {
		if (null == group) {
			return "";
		} else {
			return group;
		}
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
