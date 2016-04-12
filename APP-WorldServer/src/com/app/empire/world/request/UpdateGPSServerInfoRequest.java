package com.app.empire.world.request;

import com.app.session.Session;

public class UpdateGPSServerInfoRequest extends SessionRequest {
	/** #查找附近玩家阈值1000000倍，5000相当于查找附近+-0.5度 */
	private int threshold;
	/** #附近玩家大返回数量 */
	private int maxresults;
	/** #附近玩家列表更新时间（毫秒） */
	private int updatetime;
	/** #pagesize翻页大小 */
	private int pagesize;
	/** 玩家最大附件好友数量 */
	private int maxfriendcount;

	public UpdateGPSServerInfoRequest(int id, int sessionId, Session session, int threshold, int maxresults, int updatetime, int pagesize,
			int maxfriendcount) {
		super(IRequestType.UPDATE_ACCOUNT, id, sessionId, session);
		this.threshold = threshold;
		this.maxresults = maxresults;
		this.updatetime = updatetime;
		this.pagesize = pagesize;
		this.maxfriendcount = maxfriendcount;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getMaxresults() {
		return maxresults;
	}

	public void setMaxresults(int maxresults) {
		this.maxresults = maxresults;
	}

	public int getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(int updatetime) {
		this.updatetime = updatetime;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getMaxfriendcount() {
		return maxfriendcount;
	}

	public void setMaxfriendcount(int maxfriendcount) {
		this.maxfriendcount = maxfriendcount;
	}

}