// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.util.LinkedList;

public class CraftingRecipe
{
    protected int id;
    protected String name;
    protected String iconName;
    protected boolean isHiddenRecipe;
    protected String stationReq;
    protected boolean mustMatchLayout;
    protected int skillID;
    protected int requiredSkillLevel;
    protected int resultItemId;
    protected int resultItemCount;
    protected int recipeItemId;
    protected boolean qualityChangeable;
    protected boolean allowDyes;
    protected boolean allowEssences;
    protected int delaySeconds;
    protected LinkedList<LinkedList<CraftingComponent>> requiredCraftingComponents;
    int gridSize;
    
    public CraftingRecipe(final int id, final String name) {
        this.resultItemCount = 1;
        this.requiredCraftingComponents = new LinkedList<LinkedList<CraftingComponent>>();
        this.gridSize = 4;
        this.id = id;
        this.name = name;
    }
    
    public String getIconName() {
        return this.iconName;
    }
    
    public void setIconName(final String icon) {
        this.iconName = icon;
    }
    
    public String getStationReq() {
        return this.stationReq;
    }
    
    public void setStationReq(final String req) {
        this.stationReq = req;
    }
    
    public boolean getMustMatchLayout() {
        return this.mustMatchLayout;
    }
    
    public void setMustMatchLayout(final Boolean layoutReq) {
        this.mustMatchLayout = layoutReq;
    }
    
    public boolean getIsHiddenRecipe() {
        return this.isHiddenRecipe;
    }
    
    public void setIsHiddenRecipe(final Boolean hidden) {
        this.isHiddenRecipe = hidden;
    }
    
    public int getSkillID() {
        return this.skillID;
    }
    
    public void setSkillID(final int skill) {
        this.skillID = skill;
    }
    
    public int getRequiredSkillLevel() {
        return this.requiredSkillLevel;
    }
    
    public void setRequiredSkillLevel(final int level) {
        this.requiredSkillLevel = level;
    }
    
    public int getResultItemId() {
        return this.resultItemId;
    }
    
    public void setResultItemId(final int id) {
        this.resultItemId = id;
    }
    
    public int getResultItemCount() {
        return this.resultItemCount;
    }
    
    public void setResultItemCount(final int count) {
        this.resultItemCount = count;
    }
    
    public int getRecipeItemId() {
        return this.recipeItemId;
    }
    
    public void setRecipeItemId(final int id) {
        this.recipeItemId = id;
    }
    
    public boolean getQualityChangeable() {
        return this.qualityChangeable;
    }
    
    public void setQualityChangeable(final boolean changeable) {
        this.qualityChangeable = changeable;
    }
    
    public boolean getAllowDyes() {
        return this.allowDyes;
    }
    
    public void setAllowDyes(final boolean allow) {
        this.allowDyes = allow;
    }
    
    public boolean getAllowEssences() {
        return this.allowEssences;
    }
    
    public void setAllowEssences(final boolean allow) {
        this.allowEssences = allow;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getID() {
        return this.id;
    }
    
    public LinkedList<LinkedList<CraftingComponent>> getRequiredCraftingComponents() {
        return this.requiredCraftingComponents;
    }
    
    public void addCraftingComponentRow(final LinkedList<CraftingComponent> defs) {
        this.requiredCraftingComponents.add(defs);
    }
    
    public LinkedList<Integer> getRequiredItems() {
        final LinkedList<Integer> requiredItems = new LinkedList<Integer>();
        for (int i = 0; i < this.requiredCraftingComponents.size(); ++i) {
            for (int j = 0; j < this.requiredCraftingComponents.size(); ++j) {
                if (this.requiredCraftingComponents.get(i).get(j).itemId != -1) {
                    requiredItems.add(this.requiredCraftingComponents.get(i).get(j).itemId);
                }
            }
        }
        return requiredItems;
    }
    
    public LinkedList<Integer> getRequiredItemCounts() {
        final LinkedList<Integer> requiredCounts = new LinkedList<Integer>();
        for (int i = 0; i < this.requiredCraftingComponents.size(); ++i) {
            for (int j = 0; j < this.requiredCraftingComponents.size(); ++j) {
                if (this.requiredCraftingComponents.get(i).get(j).itemId != -1) {
                    requiredCounts.add(this.requiredCraftingComponents.get(i).get(j).count);
                }
            }
        }
        return requiredCounts;
    }
    
    public boolean DoesRecipeMatch(final LinkedList<LinkedList<CraftingComponent>> components, final String stationType) {
        Log.debug("CRAFTING: recipeMatch station: " + stationType + " against required station: " + this.stationReq);
        if (!this.stationReq.equals(stationType)) {
            return false;
        }
        for (int i = 0; i < this.requiredCraftingComponents.size(); ++i) {
            final LinkedList<CraftingComponent> reqRowComponents = this.requiredCraftingComponents.get(i);
            LinkedList<CraftingComponent> rowComponents = null;
            if (components.size() > i) {
                rowComponents = components.get(i);
            }
            for (int j = 0; j < reqRowComponents.size(); ++j) {
                final CraftingComponent reqComponent = reqRowComponents.get(j);
                if (reqComponent != null && reqComponent.itemId != -1) {
                    if (rowComponents == null || rowComponents.size() <= j) {
                        Log.debug("CRAFTING: item in row: " + i + " slot: " + j + " is null");
                        return false;
                    }
                    if (reqComponent.itemId != rowComponents.get(j).itemId) {
                        Log.debug("CRAFTING: item in row: " + i + " slot: " + j + " does not match item: " + reqComponent.itemId + " got: " + rowComponents.get(j).itemId);
                        return false;
                    }
                    if (reqComponent.count > rowComponents.get(j).count) {
                        Log.debug("CRAFTING: item in row: " + i + " slot: " + j + " does not match count for item: " + reqComponent.itemId + " got: " + rowComponents.get(j).count);
                        return false;
                    }
                }
                else if (rowComponents != null && rowComponents.size() > j && rowComponents.get(j) != null && rowComponents.get(j).itemId != -1) {
                    Log.debug("CRAFTING: item in row: " + i + " slot: " + j + " should be null but isn't, got item: " + rowComponents.get(j).itemId);
                    return false;
                }
            }
        }
        return true;
    }
    
    public int getDelaySeconds() {
        return this.delaySeconds;
    }
}
