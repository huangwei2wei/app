// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.BasicWorldNode;
import atavism.server.events.DirLocOrientEvent;
import atavism.server.events.AuthorizedLoginEvent;
import atavism.server.util.SquareQueue;
import atavism.server.engine.Event;
import java.util.ArrayList;
import java.util.LinkedList;
import atavism.server.messages.PerceptionMessage;
import java.util.Collection;
import java.util.Iterator;
import atavism.server.util.Log;
import java.util.HashMap;
import java.util.List;
import atavism.server.network.ClientConnection;
import atavism.server.engine.OID;
import java.util.Map;

public class PlayerManager
{
    private Map<OID, Player> players;
    private Map<ClientConnection, Player> conMap;
    private Map<OID, List<Perceiver>> perception;
    private int peakPlayerCount;
    private int loginCount;
    private int logoutCount;
    
    public PlayerManager() {
        this.players = new HashMap<OID, Player>();
        this.conMap = new HashMap<ClientConnection, Player>();
        this.perception = new HashMap<OID, List<Perceiver>>();
        this.peakPlayerCount = 0;
        this.loginCount = 0;
        this.logoutCount = 0;
    }
    
    public synchronized boolean addPlayer(final Player player) {
        if (this.players.containsKey(player.getOid())) {
            return false;
        }
        this.players.put(player.getOid(), player);
        this.conMap.put(player.getConnection(), player);
        if (this.players.size() > this.peakPlayerCount) {
            this.peakPlayerCount = this.players.size();
        }
        return true;
    }
    
    public synchronized Player getPlayer(final OID playerOid) {
        return this.players.get(playerOid);
    }
    
    public synchronized Player getPlayer(final ClientConnection conn) {
        return this.conMap.get(conn);
    }
    
    public synchronized Player removePlayer(final OID playerOid) {
        final Player player = this.players.remove(playerOid);
        if (player == null) {
            return null;
        }
        this.conMap.remove(player.getConnection());
        final Iterator<Map.Entry<OID, List<Perceiver>>> iterator = this.perception.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<OID, List<Perceiver>> entry = iterator.next();
            entry.getValue().remove(new Perceiver(player));
            if (entry.getValue().size() == 0) {
                iterator.remove();
            }
        }
        Log.debug("removePlayer: playerOid=" + playerOid + " perception size=" + this.perception.size());
        return player;
    }
    
    public synchronized int getPlayerCount() {
        return this.players.size();
    }
    
    public int getPeakPlayerCount() {
        return this.peakPlayerCount;
    }
    
    public int getLoginCount() {
        return this.loginCount;
    }
    
    public int getLogoutCount() {
        return this.logoutCount;
    }
    
    public synchronized int getLoginSeconds() {
        int seconds = 0;
        final long now = System.currentTimeMillis();
        for (final Player player : this.players.values()) {
            if (player.getStatus() == 2) {
                seconds += (int)((now - player.getLoginTime()) / 1000L);
            }
        }
        return seconds;
    }
    
    public synchronized void getPlayers(final Collection<Player> pp) {
        pp.addAll(this.players.values());
    }
    
    public synchronized void addWorldPerception(final Player player, final Collection<PerceptionMessage.ObjectNote> objectNotes, final List<OID> newSubjects) {
        Log.debug("PERCEP: adding world perceptions for player " + player.getName());
        for (final PerceptionMessage.ObjectNote objectNote : objectNotes) {
            List<Perceiver> perceivers = this.perception.get(objectNote.getSubject());
            if (perceivers == null) {
                perceivers = new LinkedList<Perceiver>();
                perceivers.add(new Perceiver(player));
                this.perception.put(objectNote.getSubject(), perceivers);
                Log.debug("PERCEP: added player to the list of players that can perceive: " + objectNote.getSubject());
                newSubjects.add(objectNote.getSubject());
            }
            else {
                boolean newPerceiver = true;
                for (final Perceiver perceiver : perceivers) {
                    if (perceiver.player == player) {
                        Log.debug("PERCEP: got an existing perceiver of this object for this player");
                        if (perceiver.world) {
                            Log.error("addWorldPerception: playerOid=" + player.getOid() + " already perceives oid=" + objectNote.getSubject());
                        }
                        else {
                            perceiver.world = true;
                        }
                        newPerceiver = false;
                        break;
                    }
                }
                if (!newPerceiver) {
                    continue;
                }
                perceivers.add(new Perceiver(player));
                newSubjects.add(objectNote.getSubject());
                Log.debug("PERCEP: added player to the list of players that can perceive: " + objectNote.getSubject());
            }
        }
    }
    
    public synchronized void removeWorldPerception(final Player player, final Collection<PerceptionMessage.ObjectNote> objectNotes, final List<OID> deleteSubjects) {
        Log.debug("PERCEP: removing world perception of player " + player.getName());
        for (final PerceptionMessage.ObjectNote objectNote : objectNotes) {
            final List<Perceiver> perceivers = this.perception.get(objectNote.getSubject());
            if (perceivers == null) {
                Log.error("removePerception: playerOid=" + player.getOid() + " duplicate lost oid=" + objectNote.getSubject());
            }
            else {
                final Iterator<Perceiver> iterator = perceivers.iterator();
                while (iterator.hasNext()) {
                    final Perceiver perceiver = iterator.next();
                    if (perceiver.player != player) {
                        continue;
                    }
                    if (!perceiver.world) {
                        Log.error("removeWorldPerception: playerOid=" + player.getOid() + " does not perceive oid=" + objectNote.getSubject());
                        break;
                    }
                    perceiver.world = false;
                    if (perceiver.staticCount != 0) {
                        break;
                    }
                    iterator.remove();
                    deleteSubjects.add(objectNote.getSubject());
                    Log.debug("PERCEP: player is no longer perceived by: " + objectNote.getSubject());
                    if (perceivers.size() == 0) {
                        this.perception.remove(objectNote.getSubject());
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public synchronized boolean addStaticPerception(final Player player, final OID subjectOid) {
        Log.debug("PERCEP: adding static perception of " + subjectOid + " to player " + player.getName());
        List<Perceiver> perceivers = this.perception.get(subjectOid);
        if (perceivers == null) {
            perceivers = new LinkedList<Perceiver>();
            this.perception.put(subjectOid, perceivers);
            perceivers.add(new Perceiver(player, 1));
            return true;
        }
        for (final Perceiver perceiver : perceivers) {
            if (perceiver.player == player) {
                final Perceiver perceiver2 = perceiver;
                ++perceiver2.staticCount;
                return perceiver.staticCount == 1 && !perceiver.world;
            }
        }
        perceivers.add(new Perceiver(player, 1));
        return true;
    }
    
    public synchronized boolean removeStaticPerception(final Player player, final OID playerOid) {
        Log.debug("PERCEP: removing static perception of " + playerOid + " from player " + player.getName());
        final List<Perceiver> perceivers = this.perception.get(playerOid);
        if (perceivers == null) {
            Log.error("removeStaticPerception: playerOid=" + player.getOid() + " no perceivers for oid=" + playerOid);
            return false;
        }
        final Iterator<Perceiver> iterator = perceivers.iterator();
        while (iterator.hasNext()) {
            final Perceiver perceiver = iterator.next();
            if (perceiver.player == player) {
                final Perceiver perceiver2 = perceiver;
                --perceiver2.staticCount;
                if (perceiver.staticCount == 0 && !perceiver.world) {
                    iterator.remove();
                    if (perceivers.size() == 0) {
                        this.perception.remove(playerOid);
                        return true;
                    }
                }
                return false;
            }
        }
        Log.error("removeStaticPerception: playerOid=" + player.getOid() + " does not perceive oid=" + playerOid);
        return false;
    }
    
    public synchronized List<Player> getPerceivers(final OID subjectOid) {
        final List<Perceiver> perceivers = this.perception.get(subjectOid);
        if (perceivers == null) {
            return null;
        }
        final ArrayList<Player> players = new ArrayList<Player>(perceivers.size());
        for (final Perceiver perceiver : perceivers) {
            players.add(perceiver.player);
        }
        return players;
    }
    
    public void processEvent(final Player player, final Event event, final SquareQueue<Player, Event> eventQQ) {
        final long now = System.currentTimeMillis();
        event.setEnqueueTime(now);
        synchronized (this) {
            if (Log.loggingDebug) {
                Log.debug("processEvent player " + player + " " + event.getClass().getName());
            }
            if (player == null) {
                Log.debug("PlayerManager.processEvent(): player is null");
                if (!(event instanceof AuthorizedLoginEvent)) {
                    Log.warn("PlayerManager.processEvent(): event is not an instance of AuthorizedLoginEvent");
                    return;
                }
            }
            else {
                if (player.getStatus() == 1) {
                    if (player.getDeferredEvents() == null) {
                        player.setDeferredEvents(new LinkedList<Event>());
                    }
                    Log.info("PlayerManager.processEvent(): Defering event=" + event + " because player.getStatus() is STATUS_LOGIN_PENDING");
                    player.getDeferredEvents().add(event);
                    return;
                }
                if (player.getStatus() == 3) {
                    Log.warn("PlayerManager.processEvent(): Ignoring event=" + event + " because player.getStatus() is STATUS_LOGOUT");
                    return;
                }
                if (event instanceof DirLocOrientEvent) {
                    final DirLocOrientEvent dloEvent = (DirLocOrientEvent)event;
                    final BasicWorldNode wnode = new BasicWorldNode(null, dloEvent.getDir(), dloEvent.getLoc(), dloEvent.getQuaternion());
                    Log.debug("DLO " + wnode);
                    if (!player.lastLocUpdate.equals(wnode)) {
                        player.setLastActivityTime(now);
                        player.lastLocUpdate = wnode;
                    }
                    else {
                        player.setLastContactTime(now);
                    }
                }
                else {
                    player.setLastActivityTime(now);
                }
            }
        }
        eventQQ.insert(player, event);
    }
    
    public synchronized void loginComplete(final Player player, final SquareQueue<Player, Event> eventQQ) {
        if (player.getStatus() == 3) {
            Log.error("PlayerManager.loginComplete: Aborting... player.getStatus() is STATUS_LOGOUT");
            return;
        }
        player.setStatus(2);
        player.setLoginTime(System.currentTimeMillis());
        player.setLastActivityTime(player.getLoginTime());
        if (player.getDeferredEvents() != null) {
            for (final Event event : player.getDeferredEvents()) {
                eventQQ.insert(player, event);
            }
            player.setDeferredEvents(null);
        }
        ++this.loginCount;
    }
    
    public synchronized boolean logout(final Player player) {
        final Player existingPlayer = this.players.get(player.getOid());
        if (existingPlayer == null) {
            Log.error("PlayerManager.logout: player not found: player=" + player);
            return false;
        }
        if (existingPlayer != player) {
            Log.error("PlayerManager.logout: player instance mis-match");
            return false;
        }
        if (player.getStatus() == 3) {
            return false;
        }
        player.setStatus(3);
        ++this.logoutCount;
        return true;
    }
    
    public synchronized List<Player> getTimedoutPlayers(final long activityTimeoutMS, final long contactTimeoutMS) {
        final long now = System.currentTimeMillis();
        final List<Player> timedout = new ArrayList<Player>(10);
        for (final Player player : this.players.values()) {
            if (player.getStatus() == 2 && (now - player.getLastContactTime() > contactTimeoutMS || now - player.getLastActivityTime() > activityTimeoutMS)) {
                timedout.add(player);
            }
        }
        return timedout;
    }
    
    private static class Perceiver
    {
        public Player player;
        public short staticCount;
        public boolean world;
        
        public Perceiver(final Player player) {
            this.player = player;
            this.staticCount = 0;
            this.world = true;
        }
        
        public Perceiver(final Player player, final int initialCount) {
            this.player = player;
            this.staticCount = (short)initialCount;
            this.world = false;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this.player.getOid().compareTo(((Perceiver)other).player.getOid()) == 0;
        }
        
        @Override
        public int hashCode() {
            return this.player.getOid().hashCode();
        }
    }
}
