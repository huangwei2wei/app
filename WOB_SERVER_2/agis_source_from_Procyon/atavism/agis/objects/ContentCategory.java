// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.HashMap;
import java.io.Serializable;

public class ContentCategory implements Serializable
{
    protected int id;
    protected HashMap<Integer, AgisBasicQuest> quests;
    public HashMap<Integer, Currency> currencies;
    private static final long serialVersionUID = 1L;
    
    public ContentCategory(final int id) {
        this.quests = new HashMap<Integer, AgisBasicQuest>();
        this.currencies = new HashMap<Integer, Currency>();
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public HashMap<Integer, AgisBasicQuest> getQuests() {
        return this.quests;
    }
    
    public void setQuests(final HashMap<Integer, AgisBasicQuest> quests) {
        this.quests = quests;
    }
    
    public HashMap<Integer, Currency> getCurrencies() {
        return this.currencies;
    }
    
    public void setCurrencies(final HashMap<Integer, Currency> currencies) {
        this.currencies = currencies;
    }
}
