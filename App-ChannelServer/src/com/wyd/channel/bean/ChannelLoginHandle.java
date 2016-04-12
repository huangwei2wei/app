package com.wyd.channel.bean;

import java.util.Date;

public class ChannelLoginHandle {
	private int id;
	private int state;//0处理中，1处理完
	private Date createTime;
	private ChannelLogin loginParamter;
	private ChannelLoginResult loginResult;
	public ChannelLoginHandle(ChannelLogin loginParamter) {		
		id = this.hashCode();
		createTime = new Date();
		this.loginParamter = loginParamter;
	}
	public ChannelLoginHandle(int id, ChannelLoginResult loginResult) {		
		this.id = id;
		createTime = new Date();
		this.loginResult = loginResult;
		this.state=1;
	}
	public int getId() {
		return id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public ChannelLogin getLoginParamter() {
		return loginParamter;
	}
	public void setLoginParamter(ChannelLogin loginParamter) {
		this.loginParamter = loginParamter;
	}
	public ChannelLoginResult getLoginResult() {
		return loginResult;
	}
	public void setLoginResult(ChannelLoginResult loginResult) {
		this.loginResult = loginResult;
	}	
	public String toJSON(){
		String code="",message="";
		if(loginResult!=null){
			code=loginResult.getCode();
			message=loginResult.getMessage();
		}
		String result = "{\"serialno\":"+this.id+",\"state\":"+this.state+",\"code\":\""+code+"\",\"message\":\""+message+"\"}";
		return result.replace("\"{", "{").replace("}\"", "}");
	}
	
	
}
