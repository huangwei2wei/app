// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.engine.SearchSelection;
import atavism.server.engine.SearchClause;
import atavism.server.objects.ObjectType;
import atavism.msgsys.MessageType;
import atavism.msgsys.Message;

public class SearchMessage extends Message
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
}
