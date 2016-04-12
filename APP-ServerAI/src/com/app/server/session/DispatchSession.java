package com.app.server.session;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.server.service.ServiceManager;
import com.app.session.Session;
public class DispatchSession extends Session {
	Logger log = Logger.getLogger(DispatchSession.class);
	private int id;
	private String area;
	private String group;
	private Integer serverId;
	private String address;

	public DispatchSession(IoSession session) {
		super(session);
	}
	@Override
	public void closed() {
		ServiceManager.getManager().getServerListService().removeServer(area, group, serverId, id);
	}
	@Override
	public void created() {
	}
	@Override
	public <T> void handle(T packet) {
		log.info(packet + " handle 没有找到");
	}
	@Override
	public void opened() {
	}
	@Override
	public void idle(IoSession session, IdleStatus status) {
		System.out.println("关闭链接："+session);
		session.close(true);
	}
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
