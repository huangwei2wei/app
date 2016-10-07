package com.app.empire.scene.util;

/**
 *  
 */
public class IdUtilFacotry {
	private static IdUtil	LIVING_ID_UTIL	= new IdUtil(5, 10);
	private static IdUtil	ATTACK_ID		= new IdUtil(10, 10);
	
//	public static long nextAttackId() {
//		synchronized (ATTACK_ID) {
//			return ATTACK_ID.nextId();
//		}
//	}
//
//	public static long nextMonsterId() {
//		synchronized (LIVING_ID_UTIL) {
//			return LIVING_ID_UTIL.nextId() + 100000000000000l;
//		}
//	}
}
