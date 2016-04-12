// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.Message;

public interface BracketedMessage
{
    Message getPreMessage();
    
    Message getPostMessage();
}
