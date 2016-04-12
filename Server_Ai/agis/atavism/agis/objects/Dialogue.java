// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;
import java.io.Serializable;

public class Dialogue implements Serializable
{
    int id;
    String name;
    boolean openingDialogue;
    boolean repeatable;
    int prereqDialogue;
    int prereqQuest;
    int prereqFaction;
    int prereqFactionStance;
    boolean reactionAutoStart;
    String text;
    ArrayList<DialogueOption> options;
    private static final long serialVersionUID = 1L;
    
    public Dialogue() {
        this.options = new ArrayList<DialogueOption>();
    }
    
    public Dialogue(final int id, final String name, final String text) {
        this.options = new ArrayList<DialogueOption>();
        this.id = id;
        this.name = name;
        this.text = text;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean getOpeningDialogue() {
        return this.openingDialogue;
    }
    
    public void setOpeningDialogue(final boolean openingDialogue) {
        this.openingDialogue = openingDialogue;
    }
    
    public boolean getRepeatable() {
        return this.repeatable;
    }
    
    public void setRepeatable(final boolean repeatable) {
        this.repeatable = repeatable;
    }
    
    public int getPrereqDialogue() {
        return this.prereqDialogue;
    }
    
    public void setPrereqDialogue(final int prereqDialogue) {
        this.prereqDialogue = prereqDialogue;
    }
    
    public int getPrereqQuest() {
        return this.prereqQuest;
    }
    
    public void setPrereqQuest(final int prereqQuest) {
        this.prereqQuest = prereqQuest;
    }
    
    public int getPrereqFaction() {
        return this.prereqFaction;
    }
    
    public void setPrereqFaction(final int prereqFaction) {
        this.prereqFaction = prereqFaction;
    }
    
    public int getPrereqFactionStance() {
        return this.prereqFactionStance;
    }
    
    public void setPrereqFactionStance(final int prereqFactionStance) {
        this.prereqFactionStance = prereqFactionStance;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public ArrayList<DialogueOption> getOptions() {
        return this.options;
    }
    
    public void setOptions(final ArrayList<DialogueOption> options) {
        this.options = options;
    }
    
    public void addOption(final String text, final String action, final int actionID) {
        final DialogueOption option = new DialogueOption();
        option.text = text;
        option.action = action;
        option.actionID = actionID;
        this.options.add(option);
    }
    
    public class DialogueOption
    {
        public String text;
        public String action;
        public int actionID;
    }
}
