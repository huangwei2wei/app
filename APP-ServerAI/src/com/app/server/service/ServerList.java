package com.app.server.service;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class ServerList implements Comparable<ServerList> {

	private static final int IN_MAINTANCE  = 0;		// 维护中
    private static final int NOT_IN_MAINTANCE = 1;	// 运行中
    
    private int              id;
    private String           name;
    private List<String>     address;
    
    // 当前人数
    private int              current;
    
    // 最大人数
    private int              max;
    
    // 是否在维护中
    private boolean          maintance;
    
    // 最高在线
    private int              topOnline;
    
    // 平均在线
    private int              averageOnline;
    
    // 公司码（用于在公共端口做数据分发，这里标示自己公司名称）
    private String           companyeCode;
    // 机器码（用于在公共端口做数据分法，这里标示服务器的机器名称）
    private String           machineCode;

    public ServerList() {
        this.address = new ArrayList<String>();
    }

    public void addTopOnline(int top) {
        this.topOnline += top;
    }

    public void addAverage(int average) {
        this.averageOnline += average;
    }

    public void addAddress(String address) {
        this.address.add(address);
    }

    public void addCurrent(int cur) {
        this.current += cur;
    }

    public void addMax(int max) {
        this.max += max;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddress() {
        return this.address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String toListString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("服务器名称:");
        sb.append(this.name);
        
        sb.append(" 当前在线人数:");
        sb.append(this.current).append("/").append(this.max);
        
        sb.append(" 今日平均在线人数：").append(this.averageOnline);
        sb.append(" 今日最高在线人数：").append(this.topOnline);
        
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.name);
        sb.append(",");
        for (int i = 0; i < this.address.size(); ++i) {
            String add = (String) this.address.get(i);
            sb.append(add);
            if (i < this.address.size() - 1) sb.append(";");
        }
        sb.append(",");
        double v = this.current * 1.0D / this.max;
        int load = (int) (v * 100.0D);
        sb.append(load);
        sb.append(",");
        if (isMaintance())
            sb.append(IN_MAINTANCE);
        else {
            sb.append(NOT_IN_MAINTANCE);
        }
        sb.append(",");
        sb.append(companyeCode);
        sb.append(",");
        sb.append(machineCode);
        return sb.toString();
    }

    public byte[] toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.write(this.id);
            dos.writeUTF(this.name);
            dos.write(this.address.size());
            for (String add : this.address)
                dos.writeUTF(add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public int compareTo(ServerList o) {
        return (this.id < o.id) ? -1 : (this.id == o.id) ? 0 : 1;
    }

    public int getCurrent() {
        return this.current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isMaintance() {
        return this.maintance;
    }

    public void setMaintance(boolean maintance) {
        this.maintance = maintance;
    }

    public int getTopOnline() {
        return this.topOnline;
    }

    public void setTopOnline(int topOnline) {
        this.topOnline = topOnline;
    }

    public int getAverageOnline() {
        return this.averageOnline;
    }

    public void setAverageOnline(int averageOnline) {
        this.averageOnline = averageOnline;
    }

    public String getCompanyeCode() {
        return companyeCode;
    }

    public void setCompanyeCode(String companyeCode) {
        this.companyeCode = companyeCode;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }
}
