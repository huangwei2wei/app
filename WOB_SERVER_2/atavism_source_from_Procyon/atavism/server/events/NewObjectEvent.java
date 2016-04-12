// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.math.AOVector;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class NewObjectEvent extends Event
{
    public OID objOid;
    public String objName;
    public Point objLoc;
    public Quaternion objOrient;
    public AOVector objScale;
    public int objType;
    public boolean objFollowsTerrain;
    
    public NewObjectEvent() {
    }
    
    public NewObjectEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
    }
    
    public NewObjectEvent(final AOObject notifyObj, final AOObject obj) {
        super(notifyObj);
        this.objOid = obj.getOid();
        this.objName = obj.getName();
        this.objLoc = obj.getLoc();
        this.objOrient = obj.getOrientation();
        this.objScale = obj.scale();
        this.objType = getObjectType(obj);
        if (obj.isMob() || obj.isItem()) {
            this.objFollowsTerrain = true;
        }
        else {
            this.objFollowsTerrain = false;
        }
    }
    
    @Override
    public String getName() {
        return "NewObjectEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.objOid);
        buf.putString((this.objName == null) ? "unknown" : this.objName);
        final Point loc = this.objLoc;
        buf.putPoint((loc == null) ? new Point() : loc);
        final Quaternion orient = this.objOrient;
        buf.putQuaternion((orient == null) ? new Quaternion() : orient);
        buf.putAOVector(this.objScale);
        buf.putInt(this.objType);
        buf.putInt(this.objFollowsTerrain ? 1 : 0);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.objOid = buf.getOID();
        this.objName = buf.getString();
        this.objLoc = buf.getPoint();
        this.objOrient = buf.getQuaternion();
        this.objScale = buf.getAOVector();
        this.objType = buf.getInt();
        this.objFollowsTerrain = (buf.getInt() == 1);
    }
    
    public static int getObjectType(final AOObject obj) {
        if (obj.isUser()) {
            return 3;
        }
        if (obj.isMob()) {
            return 1;
        }
        if (obj.isItem()) {
            return 2;
        }
        if (obj.isStructure()) {
            return 0;
        }
        throw new RuntimeException("NewObjectEvent: unknown obj type: " + obj);
    }
    
    public static String objectTypeToName(final int id) {
        switch (id) {
            case 0: {
                return "Structure";
            }
            case 1: {
                return "Mob";
            }
            case 2: {
                return "Item";
            }
            case 3: {
                return "User";
            }
            default: {
                return "Unknown";
            }
        }
    }
}
