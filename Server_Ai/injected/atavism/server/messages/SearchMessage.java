// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.SearchSelection;
import atavism.server.engine.SearchClause;
import atavism.server.objects.ObjectType;
import atavism.msgsys.MessageType;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.Message;

public class SearchMessage extends Message implements Marshallable
{
    public static final MessageType MSG_TYPE_SEARCH;
    private ObjectType objectType;
    private SearchClause searchClause;
    private SearchSelection selection;
    private static final long serialVersionUID = 1L;
    
    public SearchMessage() {
        this.setMsgType(SearchMessage.MSG_TYPE_SEARCH);
    }
    
    public SearchMessage(final ObjectType objectType, final SearchClause searchClause, final SearchSelection selection) {
        this.setMsgType(SearchMessage.MSG_TYPE_SEARCH);
        this.setType(objectType);
        this.setSearchClause(searchClause);
        this.setSearchSelection(selection);
    }
    
    public ObjectType getType() {
        return this.objectType;
    }
    
    public void setType(final ObjectType objectType) {
        this.objectType = objectType;
    }
    
    public SearchClause getSearchClause() {
        return this.searchClause;
    }
    
    public void setSearchClause(final SearchClause searchClause) {
        this.searchClause = searchClause;
    }
    
    public SearchSelection getSearchSelection() {
        return this.selection;
    }
    
    public void setSearchSelection(final SearchSelection selection) {
        this.selection = selection;
    }
    
    static {
        MSG_TYPE_SEARCH = MessageType.intern("ao.SEARCH");
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.objectType != null) {
            flag_bits = 1;
        }
        if (this.searchClause != null) {
            flag_bits |= 0x2;
        }
        if (this.selection != null) {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.objectType != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.objectType);
        }
        if (this.searchClause != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.searchClause);
        }
        if (this.selection != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.selection);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.objectType = (ObjectType)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.searchClause = (SearchClause)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.selection = (SearchSelection)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
