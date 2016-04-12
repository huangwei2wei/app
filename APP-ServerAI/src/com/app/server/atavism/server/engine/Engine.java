// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.app.server.atavism.server.engine.BasicInterpolator;
import com.app.server.atavism.server.engine.Engine;
import com.app.server.atavism.server.engine.Interpolator;

import com.app.server.atavism.server.util.NamedThreadFactory;

public class Engine {
	public static int ExecutorThreadPoolSize = 10;
	private static ScheduledThreadPoolExecutor executor;
	private static OIDManager oidManager = new OIDManager();
	private static Interpolator interpolator;

	public static ScheduledThreadPoolExecutor getExecutor() {
		if (Engine.executor == null) {
			Engine.executor = new ScheduledThreadPoolExecutor(Engine.ExecutorThreadPoolSize, new NamedThreadFactory("Scheduled"));
		}
		return Engine.executor;
	}

	public static OIDManager getOIDManager() {
		return oidManager;
	}
	public static Interpolator<?> getInterpolator() {
		return (Interpolator<?>) Engine.interpolator;
	}

	public static void setInterpolator(final Interpolator<?> interpolator) {
		Engine.interpolator = interpolator;
	}

	public static void setBasicInterpolatorInterval(final Integer interval) {
		Engine.interpolator = new BasicInterpolator(interval);
	}

}
