package com.app.empire.scene.util.pool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ObjectPool<T extends MemoryObject> implements Serializable {
	
	private static final long serialVersionUID = 943760723073862247L;
	//缓存最大容量
	public int MAX_SIZE = 500;
	//内存缓存
	private List<T> cache = new ArrayList<T>();
	
	public ObjectPool(int max){
		this.MAX_SIZE = max;
	}
	
	/**
	 * 添加到对象池中
	 */
	public void put(T value){
		synchronized (cache) {
			//对象池不存在
			if(!cache.contains(value) && cache.size()<MAX_SIZE){
				
				value.release();
				//添加到对象池中
				cache.add(value);
			}
		}
	}
	
	/**
	 * 从对象池中获得数据
	 */
	@SuppressWarnings("unchecked")
	public T get(Class<?> c) throws IllegalAccessException, InstantiationException {
		synchronized (cache) {
			//对象池中取对象
			T value = null;
			if(cache.size() > 0){
				value = cache.remove(0);
			}else{
				value = (T)c.newInstance();
			}
	
			return value;
		}
	}
}
