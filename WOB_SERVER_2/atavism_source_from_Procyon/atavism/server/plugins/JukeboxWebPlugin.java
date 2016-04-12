// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import java.io.Serializable;
import org.python.core.PyTuple;
import org.python.core.PyDictionary;
import java.util.HashMap;
import org.python.core.PyList;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.msgsys.GenericResponseMessage;
import atavism.msgsys.MessageType;
import atavism.msgsys.GenericMessage;
import java.util.ArrayList;
import atavism.server.engine.EnginePlugin;

public class JukeboxWebPlugin extends EnginePlugin
{
    public JukeboxWebPlugin() {
        super("JukeboxWeb");
        this.setPluginType("JukeboxWeb");
    }
    
    @Override
    public void onActivate() {
    }
    
    public ArrayList getTracks() {
        final GenericMessage msg = new GenericMessage();
        final MessageType jukeboxGetTracks = MessageType.intern("jukeboxGetTracks");
        msg.setMsgType(jukeboxGetTracks);
        final GenericResponseMessage respMsg = (GenericResponseMessage)Engine.getAgent().sendRPC(msg);
        final PyList trackData = (PyList)respMsg.getData();
        final ArrayList<HashMap<String, String>> trackList = new ArrayList<HashMap<String, String>>();
        int j = trackData.__len__();
        while (j-- > 0) {
            final PyDictionary trackInfo = (PyDictionary)trackData.__getitem__(j);
            final PyList list = trackInfo.items();
            final HashMap<String, String> trackMap = new HashMap<String, String>();
            int i = list.__len__();
            while (i-- > 0) {
                final PyTuple tup = (PyTuple)list.__getitem__(i);
                trackMap.put(tup.__getitem__(0).__str__().internedString(), tup.__getitem__(1).__str__().internedString());
            }
            trackList.add(trackMap);
        }
        return trackList;
    }
    
    public boolean addTrack(final String name, final String type, final String url, final String cost, final String description) {
        final GenericMessage msg = new GenericMessage();
        final MessageType jukeboxAddTrack = MessageType.intern("jukeboxAddTrack");
        msg.setMsgType(jukeboxAddTrack);
        msg.setProperty("name", name);
        msg.setProperty("type", type);
        msg.setProperty("url", url);
        msg.setProperty("cost", cost);
        msg.setProperty("description", description);
        final GenericResponseMessage respMsg = (GenericResponseMessage)Engine.getAgent().sendRPC(msg);
        final Integer respVal = (Integer)respMsg.getData();
        return respVal != 0;
    }
    
    public boolean deleteTrack(final String name) {
        final GenericMessage msg = new GenericMessage();
        final MessageType jukeboxDeleteTrack = MessageType.intern("jukeboxDeleteTrack");
        msg.setMsgType(jukeboxDeleteTrack);
        msg.setProperty("name", name);
        final GenericResponseMessage respMsg = (GenericResponseMessage)Engine.getAgent().sendRPC(msg);
        final Integer respVal = (Integer)respMsg.getData();
        return respVal != 0;
    }
    
    public int getMoney(final String poid) {
        final GenericMessage msg = new GenericMessage();
        final MessageType jukeboxGetFunds = MessageType.intern("jukeboxGetFunds");
        msg.setMsgType(jukeboxGetFunds);
        msg.setProperty("poid", poid);
        final GenericResponseMessage respMsg = (GenericResponseMessage)Engine.getAgent().sendRPC(msg);
        final Integer respVal = (Integer)respMsg.getData();
        return respVal;
    }
    
    public boolean addMoney(final String poid, final String money) {
        final GenericMessage msg = new GenericMessage();
        final MessageType jukeboxAddFunds = MessageType.intern("jukeboxAddFunds");
        msg.setMsgType(jukeboxAddFunds);
        msg.setProperty("poid", poid);
        msg.setProperty("money", money);
        Engine.getAgent().sendRPC(msg);
        return true;
    }
}
