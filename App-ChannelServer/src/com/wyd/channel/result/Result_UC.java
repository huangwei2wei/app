package com.wyd.channel.result;

import com.wyd.channel.bean.Data;
import com.wyd.channel.bean.State;

public class Result_UC {
    private String id;
    private State state;
    private Data data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
