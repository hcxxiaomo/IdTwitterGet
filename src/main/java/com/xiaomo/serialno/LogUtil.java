package com.xiaomo.serialno;

public class LogUtil {
	
	public static void getLog(Level l,Object o){
		if (l.equals(Level.INFO)) {
			System.out.println(o);
		}else if  (l.equals(Level.ERROR))   {
			System.err.println(o);
		}
	}
	public static void getLog(Class<?> z,Level l,Object o){
		if (l.equals(Level.INFO)) {
			System.out.println(z.getName().concat("-->").concat(o.toString()));
		}else if  (l.equals(Level.ERROR))   {
			System.err.println(z.getName().concat("-->").concat(o.toString()));
		}
	}

}

enum Level{
	INFO,ERROR
}
