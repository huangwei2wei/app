// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import java.util.List;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class SoundRegionConfig extends RegionConfig implements Serializable, Marshallable
{
    public static String RegionType;
    List<SoundData> soundData;
    private static final long serialVersionUID = 1L;
    
    public SoundRegionConfig() {
        this.soundData = null;
        this.setType(SoundRegionConfig.RegionType);
    }
    
    @Override
    public String toString() {
        return "[SoundConfig: soundData=" + this.soundData + "]";
    }
    
    public void setSoundData(final List<SoundData> soundData) {
        this.soundData = soundData;
    }
    
    public List<SoundData> getSoundData() {
        return this.soundData;
    }
    
    public boolean containsSound(final String fileName) {
        if (this.soundData == null) {
            return false;
        }
        for (final SoundData data : this.soundData) {
            if (fileName.equals(data.getFileName())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        SoundRegionConfig.RegionType = "SoundRegion";
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.soundData != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.soundData != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.soundData);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.soundData = (List<SoundData>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
