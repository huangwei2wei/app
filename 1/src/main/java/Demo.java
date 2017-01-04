package main.java;

import java.util.HashMap;

public class Demo {

	public static void main(String[] args) {
		HashMap<Integer, String> m = new HashMap<Integer, String>();
		m.put(10000, "aaaaa");
		m.put(100001, "aaaaa1");
		System.out.println(m);
	}
}
