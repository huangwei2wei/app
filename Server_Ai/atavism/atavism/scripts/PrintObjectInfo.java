// 
// Decompiled by Procyon v0.5.30
// 

package atavism.scripts;

import java.io.InputStream;
import atavism.server.objects.AOObject;
import java.io.ObjectInputStream;
import atavism.server.util.Log;
import atavism.server.engine.Database;
import atavism.server.engine.Namespace;
import atavism.server.engine.OID;

public class PrintObjectInfo
{
    public static void main(final String[] args) {
        try {
            if (args.length != 5) {
                System.err.println("need dbhost, dbname, user, password, dbid, namespace");
                System.exit(1);
            }
            final String host = args[0];
            final String dbname = args[1];
            final String user = args[2];
            final String password = args[3];
            final OID dbid = OID.parseLong(args[4]);
            final Namespace namespace = Namespace.intern(args[5]);
            final Database db = new Database();
            final String dburl = "jdbc:mysql://" + host + "/" + dbname;
            db.connect(dburl, user, password);
            final InputStream is = db.retrieveEntityDataByOidAndNamespace(dbid, namespace);
            Log.debug("retrieved character data");
            final ObjectInputStream ois = new ObjectInputStream(is);
            Log.debug("deserializing the object now");
            final AOObject obj = (AOObject)ois.readObject();
            if (Log.loggingDebug) {
                Log.debug("deserialized object: " + obj);
            }
        }
        catch (Exception e) {
            Log.exception("PrintObjectInfo.main got exception", e);
        }
    }
}
