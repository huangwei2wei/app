// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class QuestStartItemActivateHook implements ActivateHook
{
    protected int questID;
    private static final long serialVersionUID = 1L;
    
    public QuestStartItemActivateHook() {
    }
    
    public QuestStartItemActivateHook(final int questID) {
        this.setQuestID(questID);
    }
    
    public void setQuestID(final int questID) {
        if (questID < 1) {
            throw new RuntimeException("QuestStartItemActivateHook.setQuestID: bad quest");
        }
        this.questID = questID;
    }
    
    public int getQuestID() {
        return this.questID;
    }
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        return true;
    }
    
    @Override
    public String toString() {
        return "QuestStartItemActivateHook.quest=" + this.questID;
    }
}
