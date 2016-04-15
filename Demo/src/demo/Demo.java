package demo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Demo {
	private static int ComputeTileHash(final int x, final int y, final int mask) {
		final long h1 = -1918454973L;
		final long h2 = -669632447L;
		final long n = h1 * x + h2 * y;
		return (int) (n & mask);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// System.out.println((int) Math.ceil((double)6/7));
		// //
		// System.out.println(71/7);
		//
		// String str = "";
		// System.out.println(str.length());
		// System.out.println( str.equals(""));

		// AtomicLong applyRoomId = new AtomicLong(0);
		// System.out.println(applyRoomId.incrementAndGet());
		// // applyRoomId.set(0);
		// System.out.println(applyRoomId.incrementAndGet());

		// AtomicInteger i = new AtomicInteger(0);
		// System.out.println(i.getAndIncrement());

		// ConcurrentHashMap<String, String> channels = new ConcurrentHashMap<String, String>();
		//
		// String a = channels.putIfAbsent("a", "b----");
		// System.out.println(a);
		// a = channels.putIfAbsent("a", "c");
		// System.out.println(a);
		// a = channels.putIfAbsent("a", "c");
		// System.out.println(a);
		// a = channels.put("a", "c22");
		// System.out.println(a);
		//
		// System.out.println(channels.get("a"));

		// Integer[] arr = new Integer[] {7,4};
		//
		// List l = Arrays.asList(arr);
		// System.out.println(l);

		// Vector<Integer> v = new Vector<>();
		//
		// v.add(1);
		// v.add(2);
		// v.add(3);
		// v.add(4);
		// v.add(5);
		// v.add(6);
		// v.addElement(7);
		//
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.remove(0));
		// System.out.println(v.get(3));

		// System.out.println((byte) -122);
		// System.out.println((byte) 0b11111111);
		// System.out.println(System.currentTimeMillis());
		// System.out.println((int)(System.currentTimeMillis()/1000));

		// double i = (double)3 / 123;
		// System.out.println(i);

		// String str = "中";
		// try {
		// System.out.println(str.length());
		// byte[] data = str.getBytes("gb2312");
		// System.out.println(data.length);
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		//
		// Random random1 = new Random(100);
		// System.out.println(random1.nextInt());
		// System.out.println(random1.nextFloat());
		// System.out.println(random1.nextBoolean());
		// Random random2 = new Random(100);
		// System.out.println(random2.nextInt());
		// System.out.println(random2.nextFloat());
		// System.out.println(random2.nextBoolean());
		//
		// System.out.println(Math.round(2.4));

		// double discount = (7 + Math.random()*(9-7+1));
		// System.out.println(discount);

		// HashMap map = new HashMap();
		// map.put(1, "a");
		// System.out.println(map.get("1"));

		// switch ("aa") {
		// case "aa" :
		// System.out.println("------");
		// break;
		// default :
		// System.out.println("111------a");
		// break;
		// }
		// ArrayList<Integer> arr = new ArrayList<Integer>();
		// HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		// arr.addAll(map.values());
		// System.out.println(arr);
		// double val = 1.11811;
		// val = (double) Math.round(val * 100) / 100;
		//
		// System.out.println(val);
		// SimpleDateFormat format = new SimpleDateFormat("yyyy");
		// System.out.println( format.format(new Date()));
		//
		//
		// int day=Calendar.getInstance().getActualMaximum(Calendar.DATE);
		// System.out.println(day);

		// long v=3;
		//
		// long r = (v > 65535L) ? 16L : 0L;
		// v >>= (int)r;
		// long shift = (v > 255L) ? 8L : 0L;
		// v >>= (int)shift;
		// r |= shift;
		// shift = ((v > 15L) ? 4L : 0L);
		// v >>= (int)shift;
		// r |= shift;
		// shift = ((v > 3L) ? 2L : 0L);
		// v >>= (int)shift;
		// r |= shift;
		// r |= v >> 1;
		//
		//
		// System.out.println(r);
		// String str = "aaabbbccca.xml";
		// String stra = str.substring(0, str.length() - 4);
		// System.out.println(stra);
		// int i = Demo.ComputeTileHash(120, 13, 10);
		// System.out.println(i);
		// int a = 1;
		// int b = 2;
		// a = b;
		// b=3;
		// System.out.println(a);
		// System.out.println(Math.pow(4,3));

		// List<Integer> list = new ArrayList<Integer>();
		// list.add(1);
		//
		// String name = list.get(0).getClass().toString();
		// System.out.println(list.get(0).TYPE);
		// ///////////////////////////////////////////////////////////////////////////////////////////

		// GetSkillList list = new GetSkillList();
		// Field[] fs = list.getClass().getDeclaredFields();
		// for (Field field : fs) {
		// System.out.println("    --"+field.getType().getSimpleName());
		//
		// Type fc = field.getGenericType();
		// System.out.println("fc>> " + fc.getClass());
		// if (fc instanceof ParameterizedType) { // 【3】如果是泛型参数的类型
		// ParameterizedType pt = (ParameterizedType) fc;
		// System.out.println("pt>> " + pt);
		// Type t = pt.getActualTypeArguments()[0];
		// System.out.println("t::::" + t);
		// Class clazz = (Class) t; // 【4】 得到泛型里的class类型对象。
		// System.out.println(clazz);
		// try {
		// Object obj=clazz.newInstance();
		//
		//
		//
		// System.out.println(obj);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }

//		for (int i = 0; i < 10; i++) {
//			System.out.println("----------c" + i);
//			aaa : {
//				System.out.println("----------b");
//				if (i > 2) {
//					System.out.println(i);
////					 continue;
////					break bbb;
//				}
//				System.out.println("aaa");
//			}
//			bbb : {
//				System.out.println("bbbb");
//			}
//		}
		
		 Calendar cal = Calendar.getInstance();
		 int i = cal.get(Calendar.DAY_OF_WEEK);
		 System.out.println(i);
		 HashMap<String, String> m = new HashMap<String, String>();
		 m.put("1", "a");
		 m.put("2", "a");
		 
		 System.out.println(m.get(1+""));
	}

}
