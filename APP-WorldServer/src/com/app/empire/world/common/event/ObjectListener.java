package com.app.empire.world.common.event;

import java.util.EventListener;


public interface ObjectListener extends EventListener {
	
	public void onEvent(ObjectEvent event);

}
