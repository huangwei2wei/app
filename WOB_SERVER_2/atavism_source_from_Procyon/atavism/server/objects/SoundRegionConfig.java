// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

public class SoundRegionConfig extends RegionConfig implements Serializable
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
}
