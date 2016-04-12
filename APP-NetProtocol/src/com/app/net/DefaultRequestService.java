package com.app.net;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 类 <code>DefaultRequestService</code>存储<tt>Request</tt>请求
 * 
 * @since JDK 1.6
 */
public class DefaultRequestService implements IRequestService {
    private Map<Integer, IRequest> map;

    public DefaultRequestService() {
        this.map = new HashMap<Integer, IRequest>();
    }

    public void add(int requestId, IRequest request) {
        this.map.put(requestId, request);
    }

    public IRequest remove(int id) {
        return ((IRequest) this.map.remove(id));
    }
}
