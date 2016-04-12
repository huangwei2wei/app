// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.engine.Namespace;
import atavism.msgsys.MessageType;

public class TrainerClient
{
    public static final MessageType MSG_TYPE_REQ_TRAINER_INFO;
    public static final MessageType MSG_TYPE_REQ_SKILL_TRAINING;
    public static final MessageType MSG_TYPE_TRAINING_INFO;
    public static Namespace NAMESPACE;
    
    static {
        MSG_TYPE_REQ_TRAINER_INFO = MessageType.intern("ao.REQ_TRAINER_INFO");
        MSG_TYPE_REQ_SKILL_TRAINING = MessageType.intern("ao.REQ_SKILL_TRAINING");
        MSG_TYPE_TRAINING_INFO = MessageType.intern("ao.TRAINING_INFO");
        TrainerClient.NAMESPACE = null;
    }
}
