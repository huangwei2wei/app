// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class AllocNameResponseMessage extends ResponseMessage
{
    private String name;
    private static final long serialVersionUID = 1L;
    
    public AllocNameResponseMessage() {
        super(MessageTypes.MSG_TYPE_ALLOC_NAME_RESPONSE);
    }
    
    public AllocNameResponseMessage(final Message requestMessage, final String name) {
        super(MessageTypes.MSG_TYPE_ALLOC_NAME_RESPONSE, requestMessage);
        this.setName(name);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "[AllocNameResponseMessage: " + super.toString() + " name=" + this.name + "]";
    }
}
