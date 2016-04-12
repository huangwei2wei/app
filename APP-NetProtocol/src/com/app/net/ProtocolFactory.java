package com.app.net;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 类 <code>ProtocolFactory</code>游戏后台服务器协议类
 * 
 * @since JDK 1.6
 */
public class ProtocolFactory {
    private static Logger                          log              = Logger.getLogger(ProtocolFactory.class);
    private static String                          dataPackage      = "com.app.empire.net.data";
    private static String                          dataHandler      = "com.app.empire.net.handler";
    private Map<Short, Class<AbstractData>>        protocolDataBean = new HashMap<Short, Class<AbstractData>>();
    private Map<Class<AbstractData>, IDataHandler> protocolHandler  = new HashMap<Class<AbstractData>, IDataHandler>();
    private static ProtocolFactory                 instance         = null;
    private IDataHandler                           defaultHandler   = null;
    private static Class<?>                        protocolClass;

    /**
     * 初始化游戏协议，加载业务逻辑中的Data数据以及相对的Handler
     * @param protocolClass <tt>Protocol Class</tt>
     * @param dataPackage   <tt>data 包开始部分</tt>
     * @param dataHandler   <tt>Handler 包开始部分</tt>
     */
    public static void init(Class<?> protocolClass, String dataPackage, String dataHandler) {
        ProtocolFactory.protocolClass = protocolClass;
        ProtocolFactory.dataPackage = dataPackage;
        ProtocolFactory.dataHandler = dataHandler;
        getInstance();
    }

    /**
     * 返回ProtocolFactory实例
     * @return
     */
    private static ProtocolFactory getInstance() {
        if (instance == null) instance = new ProtocolFactory();
        return instance;
    }

    /**
     * 返回对应Date实例
     * @param <T>
     * @param mainType
     * @param subType
     * @return 
     */
    @SuppressWarnings({ "rawtypes", "unchecked"})
    public static <T extends AbstractData> T getProtocolDataBean(byte mainType, byte subType) {
        Class clazz = getInstance().getProtocolDataBeanClass(mainType, subType);
        if (clazz == null) return null;
        try {
            return (T) ((AbstractData) clazz.newInstance());
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 初始化ProtocolFactory
     */
    private ProtocolFactory() {
        privateInit();
    }

    /**
     * 初始化协议
     */
    @SuppressWarnings({ "rawtypes", "unchecked"})
    private void privateInit() {
        Field[] fields = protocolClass.getFields();
        Map<String, Byte> mainTypeMap = new HashMap<String, Byte>();
        for (Field field : fields) {
            String name = field.getName();
            if (name.startsWith("MAIN")) {
                try {
                    mainTypeMap.put(name, Byte.valueOf(field.getByte(protocolClass.newInstance())));
                } catch (Exception e) {
                    log.info("加载MAIN主协议错误");
                }
            } else {
                String mainType = "MAIN_" + getBeanPackage(name);
                String className = getBeanPackageName(name);
                try {
                    Byte mainValue = (Byte) mainTypeMap.get(mainType);
                    if (mainValue == null) throw new Exception("没有找到对应的MAIN_TYPE");
                    short v = (short) ((mainValue.byteValue() & 0xFF) << 8 | field.getByte(protocolClass.newInstance()) & 0xFF);
                    Class clazz = Class.forName(className);
                    this.protocolDataBean.put(Short.valueOf(v), clazz);
                    IDataHandler handler = getProtocolHandlerClassInstance(name);
                    if (handler == null)
                        log.warn("没有找到 " + name + " 对应的protocol handler");
                    else {
                        this.protocolHandler.put(clazz, handler);
//                        log.warn("协议关联 " + className + "对应Handle " + name);
                    }
                } catch (Exception e) {
                    log.error("初始化ProtocolFactory错误:" + e.toString());
                }
            }
        }
    }

    /**
     * 获取对应Date数据
     * @param mainType 主协议
     * @param subType
     * @return Class<AbstractData>>
     */
    @SuppressWarnings("rawtypes")
    public Class<?> getProtocolDataBeanClass(byte mainType, byte subType) {
        short v = (short) ((mainType & 0xFF) << 8 | subType & 0xFF);
        return ((Class) this.protocolDataBean.get(Short.valueOf(v)));
    }

    /**
     * 获取Date目录对应Class<br>
     * e.g.<br>
     * <tt>dataPackage = com.sumsharp.monster.protocol.data</tt><br>
     * <tt>typeName = ERROR_ProtocolError</tt><br>
     * 则返回结果为：<tt>com.sumsharp.monster.protocol.data.error.ProtocolError</tt>
     * @param typeName
     * @return
     */
    private String getBeanPackageName(String typeName) {
        String pkg = getBeanPackage(typeName);
        String name = getBeanName(typeName);
        return dataPackage + "." + pkg.toLowerCase() + "." + name;
    }

    /**
     * 获取Handler目录对应Class<br>
     * e.g.<br>
     * <tt>dataHandler = com.sumsharp.gameaccount.handler</tt><br>
     * <tt>typeName = ERROR_ProtocolError</tt><br>
     * 则返回结果为：<tt>com.sumsharp.gameaccount.handler.error.ProtocolErrorHandler</tt>
     * @param typeName
     * @return
     */
    private IDataHandler getProtocolHandlerClassInstance(String typeName) {
        String pkg = getBeanPackage(typeName);
        String name = getBeanName(typeName) + "Handler";
        String clazz = dataHandler + "." + pkg.toLowerCase() + "." + name;
        try {
            IDataHandler ret = (IDataHandler) Class.forName(clazz).newInstance();
            return ret;
        } catch (Exception e) {
        }
        return null;
    }

    public static void setDefaultHandler(IDataHandler defaultHandler) {
        getInstance().defaultHandler = defaultHandler;
    }

    /**
     * 获取副协议下杠线前部分字段<br>
     * e.g. <tt>typeName = ERROR_ProtocolError</tt>则返回ERROR部分字段
     * @param typeName
     * @return
     */
    private String getBeanPackage(String typeName) {
        int len = typeName.indexOf("_");
        return typeName.substring(0, len);
    }

    /**
     * 获取副协议下杠线后部分字段<br>
     * e.g. <tt>typeName = ERROR_ProtocolError</tt>则返回ProtocolError部分字段
     * @param typeName
     * @return
     */
    private String getBeanName(String typeName) {
        int len = typeName.indexOf("_");
        return typeName.substring(len + 1);
    }

    /**
     * 根据class名称返回对应Handler
     * @param data
     * @return
     */
    public static IDataHandler getDataHandler(AbstractData data) {
        return getDataHandler(data.getClass());
    }

    /**
     * 根据class名称返回对应Handler
     * @param data
     * @return
     */
    public static IDataHandler getDataHandler(Class<? extends AbstractData> data) {
        IDataHandler handler = (IDataHandler) getInstance().protocolHandler.get(data);
        if (handler == null) handler = getInstance().defaultHandler;
        return handler;
    }

    public static void main(String[] args) {
        new ProtocolFactory();
    }
}
