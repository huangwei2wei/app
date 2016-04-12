package com.app.empire.world.common.util;

import java.util.HashMap;
import java.util.Iterator;

public class KeyWordsState {
	private int id;
	private static int availableId = 0;
	KeyWordsState parent = null;
	KeyWordsState failState = null;
	boolean finalState = false;
	HashMap<Character, KeyWordsState> nextState = new HashMap<Character, KeyWordsState>();
	Character character = null;
	public static KeyWordsState root = new KeyWordsState();

	public KeyWordsState() {
		this.id = (availableId++);
	}

	public KeyWordsState(Character c) {
		this.id = (availableId++);
		this.character = c;
	}

	/**
	 * @param s
	 */
	public static void addString(String s) {
		KeyWordsState state = root;
		for (int i = 0; i < s.length(); ++i) {
			state = state.addState(s.charAt(i));
		}
		state.finalState = true;
	}

	public static void init() {
		init(root);
	}

	public static HashMap<Integer, Integer> match(String target) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int i = 0;
		int n = target.length();
		KeyWordsState state = root;
		while (i < n) {
			Character c = target.charAt(i);
			KeyWordsState nS = state.getState(c);
			if (nS == null) {
				if (state == root) {
					++i;
				} else {
					int temp = reg(map, state, i);
					if (temp == -1) {
						state = state.failState;
					} else {
						i = temp;
						state = root;
						c = target.charAt(i);
					}
					++i;
					state = findNextNode(map, state, c, i);
				}
			} else {
				++i;
				state = nS;
			}
		}
		reg(map, state, n);
		return map;
	}

	private static KeyWordsState findNextNode(HashMap<Integer, Integer> map, KeyWordsState state, Character c, int i) {
		KeyWordsState tempState = state.getState(c);
		if (tempState == null) {
			if (state == root) {
				return state;
			}
			reg(map, state, i);
			state = state.failState;
			return findNextNode(map, state, c, i);
		}
		return tempState;
	}

	private KeyWordsState addState(Character c) {
		KeyWordsState keyWordsState = this.nextState.get(c);
		if (keyWordsState == null) {
			KeyWordsState s = new KeyWordsState(c);
			s.parent = this;
			this.nextState.put(c, s);
			return s;
		}
		return keyWordsState;
	}

	private KeyWordsState getState(Character c) {
		Object obj = this.nextState.get(c);
		if (obj == null) {
			return null;
		}
		return ((KeyWordsState) obj);
	}

	private static int reg(HashMap<Integer, Integer> map, KeyWordsState state, int n) {
		int t = 0;
		while (state != root) {
			if (state.finalState) {
				t = n;
				String s = "";
				while (state != root) {
					s = state.character + s;
					state = state.parent;
					--n;
				}
				map.put(n, s.length());
				return t;
			}
			state = state.parent;
			--n;
		}
		return -1;
	}

	private static void init(KeyWordsState state) {
		Iterator<KeyWordsState> ite = state.nextState.values().iterator();
		while (ite.hasNext()) {
			KeyWordsState s1 = ite.next();
			KeyWordsState s2 = state.failState;
			while (true) {
				if (s2 == null) {
					s1.failState = root;
					break;
				}
				KeyWordsState s3 = s2.getState(s1.character);
				if (s3 != null) {
					s1.failState = s3;
					break;
				}
				s2 = s2.failState;
			}
			init(s1);
		}
	}

	public String debug(String s) {
		StringBuffer buf = new StringBuffer();
		buf.append(s);
		buf.append("+--(");
		buf.append(this.id);
		buf.append(")[");
		buf.append(this.character);
		buf.append(", ");
		KeyWordsState s1 = this.failState;
		if (s1 == null) {
			buf.append("null]\n");
		} else {
			buf.append(s1.id);
			buf.append("]\n");
		}
		Iterator<KeyWordsState> it = this.nextState.values().iterator();
		s = s + "|  ";
		while (it.hasNext()) {
			s1 = it.next();
			buf.append(s1.debug(s));
		}
		return buf.toString();
	}
}
