package com.app.net;
import java.net.SocketAddress;

import com.app.protocol.data.AbstractData;
public interface IConnector {
	public String getId();

	public boolean isConnected();

	public void send(AbstractData paramAbstractData);

	public void close();

	public SocketAddress getRemoteAddress();

}
