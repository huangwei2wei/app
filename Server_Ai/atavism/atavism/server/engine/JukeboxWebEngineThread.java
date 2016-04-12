// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

class JukeboxWebEngineThread implements Runnable
{
    @Override
    public void run() {
        final String[] args = { "-i", System.getProperty("atavism.jukebox.arg1"), System.getProperty("atavism.jukebox.arg2") };
        Engine.main(args);
    }
}
