// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.util.AORuntimeException;
import atavism.server.objects.Light;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.objects.LightData;
import atavism.server.engine.Event;

public class NewLightEvent extends Event
{
    LightData lightData;
    OID lightOid;
    
    public NewLightEvent() {
        this.lightData = null;
        this.lightOid = null;
    }
    
    public NewLightEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.lightData = null;
        this.lightOid = null;
    }
    
    public NewLightEvent(final OID notifyOid, final OID lightOid, final LightData lightData) {
        super(notifyOid);
        this.lightData = null;
        this.lightOid = null;
        this.setLightOid(lightOid);
        this.setLightData(lightData);
    }
    
    @Override
    public String getName() {
        return "NewLightEvent";
    }
    
    public void setLightData(final LightData lightData) {
        this.lightData = lightData;
    }
    
    public LightData getLightData() {
        return this.lightData;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final OID notifyObjOid = this.getObjectOid();
        final LightData lightData = this.getLightData();
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(notifyObjOid);
        buf.putInt(msgId);
        buf.putOID(this.getLightOid());
        boolean isPoint = false;
        boolean isDir = false;
        Log.debug("NewLightEvent: lightName=" + lightData.getName());
        if (lightData.getInitLoc() != null) {
            Log.debug("NewLightEvent: got lightType=" + Light.LightType.Point.ordinal());
            isPoint = true;
            buf.putInt(Light.LightType.Point.ordinal());
        }
        else {
            if (lightData.getOrientation() == null) {
                throw new AORuntimeException("NewLightEvent.toBytes: unknown light type");
            }
            Log.debug("NewLightEvent: lightType=" + Light.LightType.Directional.ordinal());
            isDir = true;
            buf.putInt(Light.LightType.Directional.ordinal());
        }
        buf.putString(lightData.getName());
        buf.putColor(lightData.getDiffuse());
        buf.putColor(lightData.getSpecular());
        buf.putFloat(lightData.getAttenuationRange());
        buf.putFloat(lightData.getAttenuationConstant());
        buf.putFloat(lightData.getAttenuationLinear());
        buf.putFloat(lightData.getAttenuationQuadradic());
        if (isPoint) {
            final Point loc = lightData.getInitLoc();
            buf.putPoint((loc == null) ? new Point() : loc);
        }
        else if (isDir) {
            final Quaternion orient = lightData.getOrientation();
            buf.putQuaternion((orient == null) ? new Quaternion() : orient);
        }
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        final OID lightOid = buf.getOID();
        final int lightType = buf.getInt();
        final LightData ld = new LightData();
        ld.setName(buf.getString());
        ld.setDiffuse(buf.getColor());
        ld.setSpecular(buf.getColor());
        ld.setAttenuationRange(buf.getFloat());
        ld.setAttenuationConstant(buf.getFloat());
        ld.setAttenuationLinear(buf.getFloat());
        ld.setAttenuationQuadradic(buf.getFloat());
        if (lightType == Light.LightType.Point.ordinal()) {
            ld.setInitLoc(buf.getPoint());
        }
        else {
            if (lightType != Light.LightType.Directional.ordinal()) {
                throw new AORuntimeException("NewLightEvent.parseBytes: only point light supported at the moment");
            }
            ld.setOrientation(buf.getQuaternion());
        }
        this.setLightOid(lightOid);
        this.setLightData(ld);
    }
    
    public OID getLightOid() {
        return this.lightOid;
    }
    
    public void setLightOid(final OID lightOid) {
        this.lightOid = lightOid;
    }
}
