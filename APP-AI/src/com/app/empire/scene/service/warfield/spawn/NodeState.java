package com.app.empire.scene.service.warfield.spawn;


public abstract class NodeState {
	public static final int	PREPARE	= 0;	// 关闭
	public static final int	START	= 1;	// 开始
	public static final int	OVER	= 2;	// 结束
	public static final int	DELETE	= 3;	// 移除
	protected long			currentMillis;
	protected int			code;
	protected SpwanNode		spawnNode;		// 所属副本

	public NodeState(SpwanNode spawnNode) {
		this.spawnNode = spawnNode;
	}

	public abstract void work();

	public int getCode() {
		return code;
	}
}
