package com.app.empire.world.entity.mongo;

import java.util.Date;

/**
 * 玩家任务实例
 * 
 * @author doter
 */
public class TaskEntry {
	private Short type;// 类型
	private Short subType;// 子类型
	private Integer nextTaskId;// 下一个任务id
	private String completeAward;// 完成任务奖励
	private Date dataTime;// 时间
	private Short status;// 状态 0未激活，１未完成，２完成，３任务奖励已领取
	private String activationCondition;// 激活条件
	private String completeCondition;// 完成条件

	public Short getType() {
		return type;
	}
	public void setType(Short type) {
		this.type = type;
	}
	public Short getSubType() {
		return subType;
	}
	public void setSubType(Short subType) {
		this.subType = subType;
	}
	public Integer getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(Integer nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	public String getCompleteAward() {
		return completeAward;
	}
	public void setCompleteAward(String completeAward) {
		this.completeAward = completeAward;
	}
	public Date getDataTime() {
		return dataTime;
	}
	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}
	public Short getStatus() {
		return status;
	}
	public void setStatus(Short status) {
		this.status = status;
	}
	public String getActivationCondition() {
		return activationCondition;
	}
	public void setActivationCondition(String activationCondition) {
		this.activationCondition = activationCondition;
	}
	public String getCompleteCondition() {
		return completeCondition;
	}
	public void setCompleteCondition(String completeCondition) {
		this.completeCondition = completeCondition;
	}

}
