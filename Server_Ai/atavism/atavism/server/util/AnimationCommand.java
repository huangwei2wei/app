// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.io.Serializable;

public class AnimationCommand implements Serializable
{
    String command;
    String animName;
    boolean isLoopFlag;
    public static final String ADD_CMD = "add";
    public static final String CLEAR_CMD = "clear";
    private static final long serialVersionUID = 1L;
    
    public AnimationCommand() {
        this.command = null;
        this.animName = null;
        this.isLoopFlag = false;
    }
    
    @Override
    public String toString() {
        return "[AnimationCommand: cmd=" + this.getCommand() + ", animName=" + this.getAnimName() + ", isLoop=" + this.isLoop() + "]";
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public String getAnimName() {
        return this.animName;
    }
    
    public void setAnimName(final String animName) {
        this.animName = animName;
    }
    
    public boolean isLoop() {
        return this.isLoopFlag;
    }
    
    public void isLoop(final boolean flag) {
        this.isLoopFlag = flag;
    }
    
    public static AnimationCommand clear() {
        final AnimationCommand ac = new AnimationCommand();
        ac.setCommand("clear");
        ac.setAnimName("");
        ac.isLoop(false);
        return ac;
    }
    
    public static AnimationCommand add(final String animName, final boolean isLoop) {
        final AnimationCommand ac = new AnimationCommand();
        ac.setCommand("add");
        ac.setAnimName(animName);
        ac.isLoop(isLoop);
        return ac;
    }
}
