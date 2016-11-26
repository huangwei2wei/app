package com.app.empire.scene.constant;

public class EquipConstant {

	/**
	 * 装备类型
	 * @author Gxf
	 *
	 */
	public interface EquipType {

		/**
	     * 武器
	     */
		public static final byte weapon = 11;
	    
	    /**
	     * 头
	     */
		public static final byte head = 12;
	    
	    /**
	     * 项链
	     */
		public static final byte necklace = 13;
	    
	    /**
	     * 身
	     */
		public static final byte body = 14;
	    
	    /**
	     * 戒指
	     */
		public static final byte ring = 15;
	    
	    /**
	     * 手
	     */
		public static final byte hand = 16;
	    
	    /**
	     * 玉佩
	     */
		public static final byte ornament = 17;
	    
	    /**
	     * 脚
	     */
		public static final byte foot = 18;

	    /**
	     * 时装武器
	     */
		public static final byte fashion_weapon = 19;
	    
	    /**
	     * 时装衣服
	     */
		public static final byte fashion_clothe = 20;
	    
	    /**
	     * 时装背饰
	     */
		public static final byte fashion_cape = 21;
	}
	
	/**
     * 装备位置
     * 
     *         
     */
    public interface EquipPosition
    {
    	/**
    	 * 武器位置
    	 */
    	byte weaponPosition = 0;
    	/**
    	 * 头冠位置
    	 */
        byte headPosition = 1;
        
        /**
         * 项链位置
         */
        byte necklacePosition = 2;
        
        /**
         * 衣服位置
         */
        byte bodyPosition = 3;
        
        /**
         * 戒指位置
         */
        byte ringPosition = 4;
        
        /**
         * 护手位置
         */
        byte handPosition = 5;
        
        /**
         * 玉佩位置
         */
        byte ornamentPosition = 6;
        
        /***
         * 鞋子位置
         */
        byte footPosition = 7;
    }
    
    /**
     * 装备开孔状态
     * 
     *         
     */
    public interface EquipHole
    {
        byte closeHole = -1; // 开打开的孔
        
        byte openHole = 0; // 已经开启的孔
    }
}
