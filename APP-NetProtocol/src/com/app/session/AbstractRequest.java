package com.app.session;
import com.app.net.IRequest;
public class AbstractRequest implements IRequest {
    protected int id;
    protected int type;

    public AbstractRequest(int id, int type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }
}
