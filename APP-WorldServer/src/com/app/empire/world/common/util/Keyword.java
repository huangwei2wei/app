package com.app.empire.world.common.util;

/**
 * 类 <code>Keyword</code>关键字基类
 * 
 * @since JDK 1.6
 */
public class Keyword {
	public String word;
	public String replacement;

	public Keyword(String w, String r) {
		this.word = w;
		this.replacement = r;
	}
}
