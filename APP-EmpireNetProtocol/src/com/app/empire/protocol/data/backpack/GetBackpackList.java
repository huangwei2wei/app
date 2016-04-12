package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取背包列表
 * 
 */
public class GetBackpackList extends AbstractData {
    public GetBackpackList(int sessionId, int serial) {
        super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList, sessionId, serial);
    }

    public GetBackpackList() {
        super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList);
    }
    
}