package demo2;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import demo.GetSkillList;

public class Program {
    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException{
        Grade grade=new Grade();
        
        
		GetSkillList list = new GetSkillList();
		Field[] fs = list.getClass().getDeclaredFields();
		for (Field field : fs) {
			Type fc = field.getGenericType();
			System.out.println("fc>> " + fc.getClass());
			if (fc instanceof ParameterizedType) { // 【3】如果是泛型参数的类型
				ParameterizedType pt = (ParameterizedType) fc;
				System.out.println("pt>> " + pt);
				Type t = pt.getActualTypeArguments()[0];
				System.out.println("t::::"+t);
				Class genericClazz = (Class) t; // 【4】 得到泛型里的class类型对象。
				 try {
				 System.out.println( genericClazz.newInstance());
				 } catch (Exception e) {
				 e.printStackTrace();
				 }
			}
		}
        
//        Field field=Grade.class.getDeclaredField("students");
        Field field=Grade.class.getDeclaredField("students");
        if(List.class.isAssignableFrom(field.getType())){
            Type type=field.getGenericType();
            //这样判断type 是不是参数化类型。 如Collection<String>就是一个参数化类型。
            if(type instanceof ParameterizedType){
                //获取类型的类型参数类型。  你可以去查看jdk帮助文档对ParameterizedType的解释。
                Class clazz=(Class)((ParameterizedType) type).getActualTypeArguments()[0];
                System.out.println(clazz);
                Object obj=clazz.newInstance();
                Object obj2=clazz.newInstance();
                System.out.println(obj);
                System.out.println(obj2);
            }
        }
    }
}
