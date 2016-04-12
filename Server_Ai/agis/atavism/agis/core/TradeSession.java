// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.util.AORuntimeException;
import atavism.server.util.Log;
import atavism.server.util.LockFactory;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.List;
import atavism.server.engine.OID;

public class TradeSession
{
    private OID trader1;
    private OID trader2;
    private List<OID> offer1;
    private List<OID> offer2;
    private boolean accepted1;
    private boolean accepted2;
    private transient Lock sessionLock;
    
    public TradeSession(final OID trader1, final OID trader2) {
        this.trader1 = null;
        this.trader2 = null;
        this.offer1 = new LinkedList<OID>();
        this.offer2 = new LinkedList<OID>();
        this.accepted1 = false;
        this.accepted2 = false;
        this.sessionLock = LockFactory.makeLock("TradeSessionLock");
        this.trader1 = trader1;
        this.trader2 = trader2;
    }
    
    public boolean setOffer(final OID trader, final List<OID> offer) {
        this.sessionLock.lock();
        try {
            if (trader.equals((Object)this.trader1)) {
                this.offer1 = offer;
            }
            else {
                if (!trader.equals((Object)this.trader2)) {
                    return false;
                }
                this.offer2 = offer;
            }
            return true;
        }
        finally {
            this.sessionLock.unlock();
        }
    }
    
    public boolean updateOffer(final OID trader, final List<OID> offer, final boolean accepted) {
        this.sessionLock.lock();
        try {
            if (!this.isTrader(trader)) {
                return false;
            }
            final List<OID> oldOffer = this.getOffer(trader);
            if (!oldOffer.equals(offer)) {
                this.setAccepted(this.getPartnerOid(trader), false);
            }
            this.setOffer(trader, offer);
            this.setAccepted(trader, accepted);
            return true;
        }
        finally {
            this.sessionLock.unlock();
        }
    }
    
    public OID getTrader1() {
        return this.trader1;
    }
    
    public OID getTrader2() {
        return this.trader2;
    }
    
    public boolean isTrader(final OID trader) {
        return trader.equals((Object)this.trader1) || trader.equals((Object)this.trader2);
    }
    
    public OID getPartnerOid(final OID trader) {
        Log.debug("TradeSession.getPartnerOid: trader=" + trader + " trader1=" + this.trader1 + " trader2=" + this.trader2);
        if (trader.equals((Object)this.trader1)) {
            return this.trader2;
        }
        if (trader.equals((Object)this.trader2)) {
            return this.trader1;
        }
        Log.error("TradeSession.getPartnerOid: trader=" + trader + " not party to this session=" + this);
        throw new AORuntimeException("invalid trader");
    }
    
    public List<OID> getOffer(final OID trader) {
        this.sessionLock.lock();
        try {
            if (trader.equals((Object)this.trader1)) {
                return this.offer1;
            }
            if (trader.equals((Object)this.trader2)) {
                return this.offer2;
            }
            Log.error("TradeSession.getOffer: trader=" + trader + " not party to this session=" + this);
            throw new AORuntimeException("invalid trader");
        }
        finally {
            this.sessionLock.unlock();
        }
    }
    
    public boolean getAccepted(final OID trader) {
        this.sessionLock.lock();
        try {
            if (trader.equals((Object)this.trader1)) {
                return this.accepted1;
            }
            if (trader.equals((Object)this.trader2)) {
                return this.accepted2;
            }
            Log.error("TradeSession.getAccepted: trader=" + trader + " not party to this session=" + this);
            throw new AORuntimeException("invalid trader");
        }
        finally {
            this.sessionLock.unlock();
        }
    }
    
    public void setAccepted(final OID trader, final boolean val) {
        this.sessionLock.lock();
        try {
            if (trader.equals((Object)this.trader1)) {
                this.accepted1 = val;
            }
            else {
                if (!trader.equals((Object)this.trader2)) {
                    Log.error("TradeSession.setAccepted: trader=" + trader + " not party to this session=" + this);
                    throw new AORuntimeException("invalid trader");
                }
                this.accepted2 = val;
            }
        }
        finally {
            this.sessionLock.unlock();
        }
        this.sessionLock.unlock();
    }
    
    public boolean isComplete() {
        this.sessionLock.lock();
        try {
            return this.accepted1 && this.accepted2;
        }
        finally {
            this.sessionLock.unlock();
        }
    }
    
    public Lock getLock() {
        return this.sessionLock;
    }
}
