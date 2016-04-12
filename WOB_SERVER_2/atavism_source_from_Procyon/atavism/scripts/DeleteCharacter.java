// 
// Decompiled by Procyon v0.5.30
// 

package atavism.scripts;

import java.util.Iterator;
import java.util.List;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import atavism.server.engine.OIDManager;
import atavism.server.engine.OID;
import atavism.server.engine.Database;

public class DeleteCharacter
{
    public static void main(final String[] args) {
        try {
            if (args.length != 6) {
                System.err.println("need dbhost, dbname, dbuser, dbpassword, atavismid, world_name");
                System.exit(1);
            }
            final Database db = new Database();
            final String host = args[0];
            final String dbname = args[1];
            final String user = args[2];
            final String password = args[3];
            final OID atavismID = OID.parseLong(args[4]);
            final String worldName = args[5];
            final String dburl = "jdbc:mysql://" + host + "/" + dbname;
            db.connect(dburl, user, password);
            Engine.setOIDManager(new OIDManager(db));
            Engine.getOIDManager().defaultChunkSize = 1;
            final List<OID> gameIDs = db.getGameIDs(worldName, atavismID);
            for (final OID gameId : gameIDs) {
                db.deleteObjectData(gameId);
                db.deletePlayerCharacter(gameId);
                if (Log.loggingDebug) {
                    Log.debug("deleted obj: " + gameId);
                }
            }
        }
        catch (Exception e) {
            Log.exception("DeleteCharacter.main got exception", e);
        }
        Log.debug("Shutting down");
    }
}
