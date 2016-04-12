package com.app.db.util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * 类 <code>Spring</code> Spring 相关操作基类
 * 
 */
public class Spring {
    private static Spring      mInstance;
    private ApplicationContext mApplicationContext;

    /**
     * 私有构造函数
     */
    private Spring() {
        mApplicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    /**
     * 返回Spring实例
     * 
     * @return Spring实例
     */
    public static Spring getInstance() {
        if (mInstance == null) {
            mInstance = new Spring();
        }
        return mInstance;
    }

    /**
     * 根据类名获取对应Bean实例
     * 
     * @param className 类名
     * @return 对应Bean实例
     */
    public Object getBean(Class<?> className) {
        String name = className.getName();
        return getBean(name.substring(name.lastIndexOf('.') + 1));
    }

    /**
     * 根据Bean名称获取对应实例
     * 
     * @param beanName  Bean名称
     * @return 对应实例
     */
    public Object getBean(String beanName) {
        return mApplicationContext.getBean(beanName);
    }

    public static void main(String[] args) {
        
    }
}
