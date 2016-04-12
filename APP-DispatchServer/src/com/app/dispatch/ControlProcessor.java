package com.app.dispatch;

import com.app.protocol.INetData;

public abstract interface ControlProcessor {
	public abstract void process(INetData paramINetData);
}