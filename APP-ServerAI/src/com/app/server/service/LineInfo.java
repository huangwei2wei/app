package com.app.server.service;
import java.text.DateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
public class LineInfo {
    private static Logger  log = Logger.getLogger(LineInfo.class);
    private int            id;
    private String         area;  // 分区
    private String         group;
    private int            serverId;
    private String         address;
    private int            currOnline;//实时在线人数
    private int            maxOnline;
    private boolean        maintance;//维护
    private int            topOnline;
    private int            totalOnline; // 用于计算一天内平均在线数
    private int            totalTimes;  // 用于计算一天内平均在线数
    private String          version;
    private String         updateurl;
    private String         appraisal;//评论地址
    private int            dayMaxOnline;//当天最高在线人数    
    private Calendar       dayCal;  //记录单位时间。用于统计一天的数据。一天只设置一次值
    

    public int getDayMaxOnline() {
		return dayMaxOnline;
	}

	public int getTopOnline() {
        return this.topOnline;
    }
	//平均在线
    public int getAverageOnline() {
        return (int) (Double.valueOf(this.totalOnline)  / this.totalTimes);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return this.area;
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

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCurrOnline() {
        return this.currOnline;
    }

    public void setCurrOnline(int currOnline) {
    	this.currOnline = currOnline;
        Calendar cal = Calendar.getInstance();
        //跨天时清空
        if ((dayCal == null) || (dayCal.get(Calendar.DAY_OF_YEAR) != cal.get(Calendar.DAY_OF_YEAR))) {
        	if (dayCal != null) {
        		log.info("[ONLINE][" + DateFormat.getInstance().format(this.dayCal.getTime()) + "] Day Average: [" + getAverageOnline() + "] Top Online [" + this.topOnline + "] Day Max: ["+dayMaxOnline+"]");
            }
           	this.dayCal = Calendar.getInstance();
           	this.dayMaxOnline=0;
            this.totalOnline = 0;
            this.totalTimes = 0;
        }
        if (currOnline > 0) {
            this.totalOnline += currOnline;
            this.totalTimes += 1;
            if (currOnline > this.topOnline) this.topOnline = currOnline;
            if (currOnline > this.dayMaxOnline) this.dayMaxOnline = currOnline;
        }
    }

    public int getMaxOnline() {
        return this.maxOnline;
    }

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
    }

    public boolean getMaintance() {
        return this.maintance;
    }

    public void setMaintance(boolean maintance) {
        this.maintance = maintance;
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

    public String getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(String appraisal) {
        this.appraisal = appraisal;
    }
}
