package com.wyd.channel.utils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.wyd.channel.bean.ChannelLoginHandle;
import com.wyd.channel.servlet.ChannelService;
/**
 * 清理过期的handle
 * @author zengxc
 *
 */
public class TaskThread implements Runnable {
	Logger log = Logger.getLogger(TaskThread.class);
	private int timeout = 30*1000;

	public void setTimeout(int timeout) {
		this.timeout = timeout*1000;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Calendar cal = Calendar.getInstance();
				long nowlong = cal.getTime().getTime();
				ChannelService service = ChannelService.getInstance();
				ConcurrentHashMap<Integer, ChannelLoginHandle> maps = service.getHandleMap();
				Collection<ChannelLoginHandle> c = maps.values();
		        Iterator<ChannelLoginHandle> it = c.iterator();
		        for (; it.hasNext();) {
		        	ChannelLoginHandle handle =  it.next();		        	
		        	long cha = nowlong-handle.getCreateTime().getTime();
		        	if(cha>timeout){
		        		log.info(handle.getId()+" 登陆超时("+cha+")");
		        		service.remove(handle);
		        	}		        	
		        }
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
