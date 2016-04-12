package com.app.empire.gameaccount.session;
import java.net.SocketAddress;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import com.app.protocol.data.AbstractData;
import com.app.session.Session;

public class AcceptSession extends Session {
	protected String worldServerId;
	protected boolean valid = false;

	public String getWorldServerId() {
		return worldServerId;
	}

	public void setWorldServerId(String worldServerId) {
		this.worldServerId = worldServerId;
	}

	public AcceptSession(IoSession session) {
		this.session = session;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void send(AbstractData message) {
		if ((this.session != null) && (this.session.isConnected())) {
			this.session.write(message);
		}
	}

	public void close() {
		if ((this.session != null) && (this.session.isConnected())) {
			this.session.close(true);
		}
	}

	public SocketAddress getRemoteAddress() {
		if (this.session != null) {
			return this.session.getRemoteAddress();
		}
		return null;
	}

	@Override
	public <T> void handle(T paramT) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void created() {
		// TODO Auto-generated method stub

	}

	@Override
	public void opened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void idle(IoSession session, IdleStatus status) {
		System.out.println("关闭链接：" + session + " worldServerId:" + worldServerId);
		session.close(true);
	}
}
