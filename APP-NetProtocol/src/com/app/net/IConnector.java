package com.app.net;

import java.net.SocketAddress;

import com.app.protocol.data.AbstractData;
import com.google.protobuf.Message;

public interface IConnector {
	public String getId();

	public boolean isConnected();

	public void send(AbstractData paramAbstractData);

	public void send(short type, short subType, Message msg, byte target);

	public void send(short type, short subType, int sessionId, int serial, Message msg, byte target);

	public void close();

	public SocketAddress getRemoteAddress();

}
