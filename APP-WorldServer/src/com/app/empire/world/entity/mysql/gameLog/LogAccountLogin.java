package com.app.empire.world.entity.mysql.gameLog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * LogAccountLogin entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "log_account_login")
public class LogAccountLogin implements java.io.Serializable {
	private static final long serialVersionUID = 806667511814970240L;
	private Integer id;
	private Integer accountId;
	private String accountName;
	private Date dateTime;

	// Constructors

	/** default constructor */
	public LogAccountLogin() {
	}

	/** full constructor */
	public LogAccountLogin(Integer id, String accountName, Date dateTime) {
		this.id = id;
		this.accountName = accountName;
		this.dateTime = dateTime;
	}
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, precision = 10)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "accountId", nullable = false)
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Column(name = "accountName", nullable = false, length = 250)
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	@Column(name = "dateTime", nullable = false)
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}