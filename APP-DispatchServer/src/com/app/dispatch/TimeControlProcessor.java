package com.app.dispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.app.dispatch.vo.ClientInfo;
import com.app.empire.protocol.Protocol;
import com.app.protocol.INetData;

/***
 * 执行worldServer 发来的协议(内部处理)
 * 
 * @author doter
 * 
 */
public class TimeControlProcessor implements ControlProcessor, Runnable {
	private static TimeControlProcessor controlProcessor = new TimeControlProcessor();
	private static final Logger log = Logger.getLogger(TimeControlProcessor.class);
	public static final short ADMIN_ADDIP = 243;
	public static final short FINITERELOAD = 195;
	private ChannelService channelService;// 通道服务
	private Dispatcher dispatcher;
	private IpdService ipdService;
	// private TrustIpService trustIpService;
	private ConfigMenger configuration;
	private BlockingQueue<INetData> datas = new LinkedBlockingQueue<INetData>();

	private TimeControlProcessor() {
	}

	public static TimeControlProcessor getControlProcessor() {
		return controlProcessor;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setName("Control");
		thread.start();
		log.info("TimeControlProcessor Control start.");
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public void setConfiguration(ConfigMenger configuration) {
		this.configuration = configuration;
	}

	// public void setTrustIpService(TrustIpService trustIpService) {
	// this.trustIpService = trustIpService;
	// }
	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void setIpdService(IpdService ipdService) {
		this.ipdService = ipdService;
	}

	/** 添加执行任务 */
	public void process(INetData data) {
		try {
			datas.put(data);
		} catch (InterruptedException ex1) {
			log.info("TimeControlProcessor process INetData Exception.");
		}
	}

	/** 任务处理 */
	protected void process0(INetData data) {
		short type = data.getType();
		try {
			switch (type) {
			case Protocol.MAIN_SERVER:// 服务器间协议
				processServerMsg(data);
				break;
			case Protocol.MAIN_CHAT:
				processChannelMsg(data);
				break;
			}
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}

	public void run() {
		while (true) {
			try {
				// 检索并移除此队列datas的头部，如果此队列不存在任何元素，则一直等待。
				INetData data = (INetData) datas.take();
				process0(data);
			} catch (InterruptedException ex) {
				log.error(ex, ex);
			}
		}
	}

	private void processServerMsg(INetData data) {
		short type = data.getSubType();
		try {
			switch (type) {
			case Protocol.SERVER_SyncPlayer:
				// syncPlayer(data);// 同步玩家基本数据
				break;
			case Protocol.SERVER_NotifyMaintance: // '\037'
				maintance(data);// 设置服务器状态状态
				break;
			case Protocol.SERVER_NotifyMaxPlayer: // '\031'
				maxPlayer(data);
				break;
			case Protocol.SERVER_BroadPb:// 广播指定的玩家
				broadPb(data);
				break;
			case Protocol.SERVER_BroadCast: // '\027'
				broadcast(data);
				break;
			case Protocol.SERVER_ForceBroadCast: // '\028'
				forceBroadcast(data);
				break;
			case Protocol.SERVER_Kick: // '\029' 提玩家下线
				kick(data);
				break;
			case Protocol.SERVER_UpdateServerInfo: // '\091'
				updateServerInfo(data);
				break;
			}
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}

	/**
	 * 设置服务器状态状态
	 * 
	 * @param data
	 */
	private void maintance(INetData data) {
		try {
			boolean maintance = data.readBoolean();
			if (ipdService != null)
				ipdService.notifyIPD(-1, -1, maintance);
			// configuration.setProperty("maintance",
			// Boolean.valueOf(maintance));
			log.info("maintance:" + maintance);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	private void processChannelMsg(INetData data) {
		short subType = data.getSubType();
		try {
			switch (subType) {
			case Protocol.CHAT_SyncChannels:// 通讯频道设置
				syncChannels(data);
				break;
			case Protocol.CHAT_RemoveChannels:// 移除频道
				removeChannels(data);
				break;
			}
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}

	/** 玩家频道设置 如聊天 */
	private void syncChannels(INetData data) throws Exception {
		int sessionId = data.readInt();// 玩家session id
		IoSession session = dispatcher.getSession(sessionId);
		if (session == null)
			return;

		String addChannels[] = data.readStrings();
		String removeChannels[] = data.readStrings();
		for (String str : addChannels) {// 添加
			Channel channel = channelService.getAndCreate(str);
			channel.join(session);
		}
		for (String str : removeChannels) {// 移除
			Channel channel = channelService.getChannel(str);
			if (channel != null) {
				int size = channel.removeSession(session);
				if (size <= 0)
					channelService.removeChannel(str);
			}
		}
	}

	private void removeChannels(INetData data) throws Exception {
		String name = data.readString();
		channelService.removeChannel(name);
	}

	/**
	 * 广播线上所有用户 此方法调用 SocketDispatcher中的broadcast方法
	 * 
	 * @see com.app.dispatch.SocketDispatcher
	 * @param data
	 * @throws Exception
	 */
	private void forceBroadcast(INetData data) throws Exception {
		dispatcher.broadcast(IoBuffer.wrap(data.readBytes()));
	}

	/**
	 * 读取对应通道，向该通道上广播数据
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void broadcast(INetData data) throws Exception {
		Channel channel = channelService.getChannel(data.readString());
		if (channel != null)
			channel.broadcast(IoBuffer.wrap(data.readBytes()));
	}

	/**
	 * 向指定的玩家广播数据
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void broadPb(INetData data) throws Exception {
		int[] playerIds = data.readInts();
		if (playerIds.length > 0) {
			AllPlayer allPlayer = AllPlayer.getAllPlayer();
			for (int id : playerIds) {
				IoSession session = allPlayer.getSessions().get(id);
				if (session != null) {
					session.write(IoBuffer.wrap(data.readBytes()));
				}
			}
		}
	}

	/**
	 * 读取客户端返回的游戏人数,服务器状态
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void maxPlayer(INetData data) throws Exception {
		int current = data.readInt();
		int maxPlayer = data.readInt();
		long c = data.readLong();
		log.info((new StringBuilder()).append("SyncTime[").append(System.currentTimeMillis() - c).append("] ONLINE[").append(current).append("] MAX[").append(maxPlayer)
				.append("]").toString());
		if (ipdService != null) {
			ipdService.notifyIPD(current, maxPlayer, configuration.getConfiguration().getBoolean("maintance", true));
		}
	}

	/**
	 * 更新服务器信息
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void updateServerInfo(INetData data) throws Exception {
		String area = data.readString();
		int machine = data.readInt();
		data.readInt();
		String version = data.readString();
		String updateurl = data.readString();
		String remark = data.readString();
		String appraisal = data.readString();
		String group = data.readString();
		if (ipdService != null) {
			ipdService.updateServerInfo(area, group, machine, version, updateurl, remark, appraisal);
		}
	}

	/** 踢玩家下线 */
	private void kick(INetData data) throws Exception {
		int sessionId = data.readInt();
		dispatcher.unRegisterClient(sessionId);
	}
}