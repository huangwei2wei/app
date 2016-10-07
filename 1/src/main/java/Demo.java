package main.java;


public class Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("as");
		String t=Thread.currentThread().getContextClassLoader().getResource("").getPath();
		System.out.println("t---"+t);
		
//		String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//		System.out.println(filePath+"--");
		
	}

}
