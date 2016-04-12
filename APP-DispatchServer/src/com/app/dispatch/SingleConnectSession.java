package com.app.dispatch;
import java.net.SocketAddress;
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
public class SingleConnectSession implements IoSession {
	private int sessionId;
	private IoSession session;

	public SingleConnectSession(IoSession session, int sessionId) {
		this.session = session;
		this.sessionId = sessionId;
	}

	public int getSessionId() {
		return this.sessionId;
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

	public WriteFuture write(Object object) {
		if (this.session != null) {
			byte[] bytes = ((IoBuffer) object).array();
			byte[] bytes1 = new byte[bytes.length];
			System.arraycopy(bytes, 0, bytes1, 0, bytes.length);
			IoBuffer buf = IoBuffer.wrap(bytes1);
			buf.putInt(4, this.sessionId);
			return this.session.write(buf);
		}
		return null;
	}

	public CloseFuture close() {
		return null;
	}

	public Object getAttachment() {
		return null;
	}

	public Object setAttachment(Object object) {
		return null;
	}

	public Object getAttribute(String string) {
		return null;
	}

	public Object setAttribute(String string, Object object) {
		return null;
	}

	public Object setAttribute(String string) {
		return null;
	}

	public Object removeAttribute(String string) {
		return null;
	}

	public boolean containsAttribute(String string) {
		return false;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Set getAttributeKeys() {
		return null;
	}

	public boolean isConnected() {
		return false;
	}

	public boolean isClosing() {
		return false;
	}

	public CloseFuture getCloseFuture() {
		return null;
	}

	public SocketAddress getRemoteAddress() {
		return null;
	}

	public SocketAddress getLocalAddress() {
		return null;
	}

	public SocketAddress getServiceAddress() {
		return null;
	}

	public int getWriteTimeout() {
		return 0;
	}

	public long getWriteTimeoutInMillis() {
		return 0L;
	}

	public void setWriteTimeout(int _int) {
	}

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

	public long getCreationTime() {
		return 0L;
	}

	public long getLastIoTime() {
		return 0L;
	}

	public long getLastReadTime() {
		return 0L;
	}

	public long getLastWriteTime() {
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
	public IoFilterChain getFilterChain() {

		return null;
	}

	@Override
	public long getId() {

		return 0;
	}

	@Override
	public int getIdleCount(IdleStatus arg0) {

		return 0;
	}

	@Override
	public long getLastBothIdleTime() {

		return 0;
	}

	@Override
	public long getLastIdleTime(IdleStatus arg0) {

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
	public boolean isIdle(IdleStatus arg0) {

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