// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.util.Log;

public class Pinger implements Runnable
{
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    final Database db = Engine.getDatabase();
                    if (db != null) {
                        db.ping();
                    }
                    Thread.sleep(300000L);
                }
            }
            catch (Exception e) {
                Log.exception("Pinger.run caught exception", e);
                continue;
            }
            break;
        }
    }
}
