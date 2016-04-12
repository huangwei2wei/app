// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import atavism.server.plugins.WorldManagerClient;
import atavism.server.network.AOByteBuffer;

public interface VoiceSender
{
    void sendAllocateVoice(final VoiceConnection p0, final VoiceConnection p1, final byte p2, final boolean p3);
    
    void sendAllocateVoice(final VoiceConnection p0, final VoiceConnection p1, final byte p2, final byte p3);
    
    void sendDeallocateVoice(final VoiceConnection p0, final VoiceConnection p1, final byte p2);
    
    void sendVoiceFrame(final VoiceConnection p0, final VoiceConnection p1, final byte p2, final byte p3, final AOByteBuffer p4, final short p5);
    
    void sendExtensionMessage(final WorldManagerClient.ExtensionMessage p0);
}
