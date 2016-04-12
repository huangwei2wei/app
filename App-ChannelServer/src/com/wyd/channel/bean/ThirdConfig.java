package com.wyd.channel.bean;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the payments database table.
 * 
 * @author BEA Workshop
 */
@Entity()
@Table(name="third_configs")
public class ThirdConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private Integer appId; 
    private java.sql.Timestamp createAt;
    private String channelId;
    private String name;
    private String gameId;
    private String gameKey;
    private String reserve1;
    private String reserve2;
    
    @Id()
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false, precision=10)
    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Basic()
    @Column(name="create_at")
        public java.sql.Timestamp getCreateAt() {
        return createAt;
    }
    public void setCreateAt(java.sql.Timestamp createAt) {
        this.createAt = createAt;
    }
    
    @Basic()
    @Column(name="channel_id", length=20)
    public String getChannelId() {
        return channelId;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    @Basic()
    @Column(name="game_id", length=50)
    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    @Basic()
    @Column(name="game_key", length=100)
    public String getGameKey() {
        return gameKey;
    }
    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }
    
    @Basic()
    @Column(name="name", length=20)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Basic()
    @Column(name="reserve1", length=50)
    public String getReserve1() {
        return reserve1;
    }
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }
    
    @Basic()
    @Column(name="reserve2", length=50)
    public String getReserve2() {
        return reserve2;
    }
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }
    @Basic()
    @Column(name="app_id", length=20)
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
   
    
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ThirdConfig)) {
            return false;
        }
        ThirdConfig castOther = (ThirdConfig)other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }
    
    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }   

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }
    
}
