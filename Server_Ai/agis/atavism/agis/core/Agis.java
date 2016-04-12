// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.objects.LootTable;
import atavism.agis.objects.Currency;
import atavism.agis.objects.Faction;
import atavism.agis.objects.SkillTemplate;
import atavism.server.engine.Manager;

public class Agis
{
    public static Manager<AgisAbility> AbilityManager;
    public static Manager<AgisEffect> EffectManager;
    public static Manager<SkillTemplate> SkillManager;
    public static Manager<Faction> FactionManager;
    public static Manager<Currency> CurrencyManager;
    public static Manager<LootTable> LootTableManager;
    private static int defaultCorpseTimeout;
    
    static {
        Agis.AbilityManager = (Manager<AgisAbility>)new Manager("AbilityManager");
        Agis.EffectManager = (Manager<AgisEffect>)new Manager("EffectManager");
        Agis.SkillManager = (Manager<SkillTemplate>)new Manager("SkillManager");
        Agis.FactionManager = (Manager<Faction>)new Manager("FactionManager");
        Agis.CurrencyManager = (Manager<Currency>)new Manager("CurrencyManager");
        Agis.LootTableManager = (Manager<LootTable>)new Manager("LootTableManager");
        Agis.defaultCorpseTimeout = 60000;
    }
    
    public static int getDefaultCorpseTimeout() {
        return Agis.defaultCorpseTimeout;
    }
    
    public static void setDefaultCorpseTimeout(final int timeout) {
        Agis.defaultCorpseTimeout = timeout;
    }
}
