package com.app.dispatch;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

public class HttpSession implements IoSession {

	private List<Object> messages = Collections.synchronizedList(new LinkedList<Object>());
	private HttpAcceptor acceptor;
	SocketAddress address;
	private long lastReadTime = 0L;
	private boolean isClosing = false;
	private int sessionId;

	public HttpSession(HttpAcceptor acceptor, SocketAddress address, int sessionId) {
		this.acceptor = acceptor;
		this.address = address;
		this.lastReadTime = System.currentTimeMillis();
		this.sessionId = sessionId;
	}

	public IoService getService() {
		return null;
	}

	public IoHandler getHandler() {
		return null;
	}

	public IoSessionConfig getConfig() {
		return null;
	}

	public IoFilterChain getFilterChain() {
		return null;
	}

	public WriteFuture write(Object message) {
		synchronized (this) {
			this.messages.add(message);
			if (this.messages.size() >= 3) {
				super.notify();
			}
		}
		return null;
	}

	public IoBuffer[] getSegments() {
		synchronized (this) {
			IoBuffer[] ret = new IoBuffer[this.messages.size()];
			this.messages.toArray(ret);
			this.messages.clear();
			return ret;
		}
	}

	public CloseFuture close() {
		this.isClosing = true;
		this.acceptor.notifyClose(this);
		return null;
	}

	public Object getAttachment() {
		return null;
	}

	public Object setAttachment(Object attachment) {
		return null;
	}

	public Object getAttribute(String key) {
		return null;
	}

	public Object setAttribute(String key, Object value) {
		return null;
	}

	public Object setAttribute(String key) {
		return null;
	}

	public Object removeAttribute(String key) {
		return null;
	}

	public boolean containsAttribute(String key) {
		return false;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Set getAttributeKeys() {
		return null;
	}

	// public TransportType getTransportType() {
	// return null;
	// }

	public boolean isConnected() {
		return false;
	}

	public boolean isClosing() {
		return this.isClosing;
	}

	public CloseFuture getCloseFuture() {
		return null;
	}

	public SocketAddress getRemoteAddress() {
		return this.address;
	}

	public SocketAddress getLocalAddress() {
		return null;
	}

	public SocketAddress getServiceAddress() {
		return null;
	}

	public int getIdleTime(IdleStatus status) {
		return 0;
	}

	public long getIdleTimeInMillis(IdleStatus status) {
		return 0L;
	}

	public void setIdleTime(IdleStatus status, int idleTime) {
	}

	public int getWriteTimeout() {
		return 0;
	}

	public long getWriteTimeoutInMillis() {
		return 0L;
	}

	public void setWriteTimeout(int writeTimeout) {
	}

	// public TrafficMask getTrafficMask() {
	// return null;
	// }
	//
	// public void setTrafficMask(TrafficMask trafficMask) {
	// }

	public void suspendRead() {
	}

	public void suspendWrite() {
	}

	public void resumeRead() {
	}

	public void resumeWrite() {
	}

	public long getReadBytes() {
		return 0L;
	}

	public long getWrittenBytes() {
		return 0L;
	}

	public long getReadMessages() {
		return 0L;
	}

	public long getWrittenMessages() {
		return 0L;
	}

	public long getWrittenWriteRequests() {
		return 0L;
	}

	public int getScheduledWriteRequests() {
		return 0;
	}

	// public int getScheduledWriteBytes() {
	// return 0;
	// }

	public long getCreationTime() {
		return 0L;
	}

	public long getLastIoTime() {
		return 0L;
	}

	public void setLastReadTime(long time) {
		this.lastReadTime = time;
	}

	public long getLastReadTime() {
		return this.lastReadTime;
	}

	public int getSessionId() {
		return this.sessionId;
	}

	public long getLastWriteTime() {
		return 0L;
	}

	public boolean isIdle(IdleStatus status) {
		return false;
	}

	public int getIdleCount(IdleStatus status) {
		return 0;
	}

	public long getLastIdleTime(IdleStatus status) {
		return 0L;
	}

	@Override
	public CloseFuture close(boolean arg0) {

		return null;
	}

	@Override
	public boolean containsAttribute(Object arg0) {

		return false;
	}

	@Override
	public Object getAttribute(Object arg0) {

		return null;
	}

	@Override
	public Object getAttribute(Object arg0, Object arg1) {

		return null;
	}

	@Override
	public int getBothIdleCount() {

		return 0;
	}

	@Override
	public Object getCurrentWriteMessage() {

		return null;
	}

	@Override
	public WriteRequest getCurrentWriteRequest() {

		return null;
	}

	@Override
	public long getId() {

		return 0;
	}

	@Override
	public long getLastBothIdleTime() {

		return 0;
	}

	@Override
	public long getLastReaderIdleTime() {

		return 0;
	}

	@Override
	public long getLastWriterIdleTime() {

		return 0;
	}

	@Override
	public double getReadBytesThroughput() {

		return 0;
	}

	@Override
	public double getReadMessagesThroughput() {

		return 0;
	}

	@Override
	public int getReaderIdleCount() {

		return 0;
	}

	@Override
	public long getScheduledWriteBytes() {

		return 0;
	}

	@Override
	public int getScheduledWriteMessages() {

		return 0;
	}

	@Override
	public TransportMetadata getTransportMetadata() {

		return null;
	}

	@Override
	public WriteRequestQueue getWriteRequestQueue() {

		return null;
	}

	@Override
	public int getWriterIdleCount() {

		return 0;
	}

	@Override
	public double getWrittenBytesThroughput() {

		return 0;
	}

	@Override
	public double getWrittenMessagesThroughput() {

		return 0;
	}

	@Override
	public boolean isBothIdle() {

		return false;
	}

	@Override
	public boolean isReadSuspended() {

		return false;
	}

	@Override
	public boolean isReaderIdle() {

		return false;
	}

	@Override
	public boolean isWriteSuspended() {

		return false;
	}

	@Override
	public boolean isWriterIdle() {

		return false;
	}

	@Override
	public ReadFuture read() {

		return null;
	}

	@Override
	public Object removeAttribute(Object arg0) {

		return null;
	}

	@Override
	public boolean removeAttribute(Object arg0, Object arg1) {

		return false;
	}

	@Override
	public boolean replaceAttribute(Object arg0, Object arg1, Object arg2) {

		return false;
	}

	@Override
	public Object setAttribute(Object arg0) {

		return null;
	}

	@Override
	public Object setAttribute(Object arg0, Object arg1) {

		return null;
	}

	@Override
	public Object setAttributeIfAbsent(Object arg0) {

		return null;
	}

	@Override
	public Object setAttributeIfAbsent(Object arg0, Object arg1) {

		return null;
	}

	@Override
	public void setCurrentWriteRequest(WriteRequest arg0) {

	}

	@Override
	public void updateThroughput(long arg0, boolean arg1) {

	}

	@Override
	public WriteFuture write(Object arg0, SocketAddress arg1) {

		return null;
	}

	// @Override
	// public boolean isSecured() {
	// // TODO Auto-generated method stub
	// return false;
	// }

 
}