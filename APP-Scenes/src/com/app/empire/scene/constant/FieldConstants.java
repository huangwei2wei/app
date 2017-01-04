package com.app.empire.scene.constant;

public class FieldConstants {
    /**
     * 场景PK类型
     * @author Joseph
     *
     */
    public interface BattleType{
    	
    	/**
    	 * 安全区
    	 */
    	public static final int SAFETY 		= 0;
    	
    	/**
    	 * 可战斗，但要记pk值
    	 */
    	public static final int FIGHT		= 1;
    	
    	/**
    	 * 竞技，不记PK值
    	 */
    	public static final int ARENA		= 2;
    	
    	/**
    	 * 帮派团战地图，不记PK值
    	 */
    	public static final int GUILD		= 3;
    }
    
    /**
     * 地图PK规则判断结果
     * @author Joseph
     *
     */
    public interface FieldAttackRule{
    	
    	/** 地图不作强制处理，需要判断玩家模式 */
    	public static final int USEPLAYERMODE = 0;
    	
    	/** 地图规则强制可以攻击，不记PK值 */
    	public static final int ATTACK = 1;
    	
    	/** 地图规则强制不能攻击 */
    	public static final int UNATTACK = 2;
    }
}
