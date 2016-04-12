package com.app.protocol.handler;
import com.app.protocol.data.AbstractData;
/**
 * 接口 <code>IDataHandler</code>，定义逻辑处理接口，内部定义了handle方法。
 * 
 * @since JDK 1.7
 * @author doter
 */

public interface IDataHandler {
	public AbstractData handle(AbstractData paramAbstractData) throws Exception;
}
