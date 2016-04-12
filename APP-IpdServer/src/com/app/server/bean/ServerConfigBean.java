package com.app.server.bean;
 
public class ServerConfigBean { 
    private String area;
    private String group;
    private String name;
    private int random;
    private int istest;
    private int openudid;
    private String bulletin;
    private int   serverId;//机器id
    private int order;
    
    
    
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getRandom() {
        return random;
    }
    public void setRandom(int random) {
        this.random = random;
    }
    public String getBulletin() {
        return bulletin;
    }
    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }
    public int getIstest() {
        return istest;
    }
    public void setIstest(int istest) {
        this.istest = istest;
    }
    public int getOpenudid() {
        return openudid;
    }
    public void setOpenudid(int openudid) {
        this.openudid = openudid;
    }
    public int getServerId() {
        return serverId;
    }
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
    
}
