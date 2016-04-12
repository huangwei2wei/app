// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;
import atavism.server.engine.OID;
import java.io.Serializable;

public class Mail implements Serializable
{
    int id;
    String recipientName;
    OID recipientOID;
    String senderName;
    OID senderOID;
    String subject;
    String message;
    int currencyType;
    int currencyAmount;
    ArrayList<OID> items;
    int itemCategory;
    boolean CoD;
    boolean mailRead;
    boolean mailArchive;
    private static final long serialVersionUID = 1L;
    
    public Mail() {
    }
    
    public Mail(final int id, final OID recipientOID, final String recipientName, final OID senderOID, final String senderName, final String subject, final String message, final int currencyType, final int currencyAmount, final ArrayList<OID> items, final int category, final boolean CoD) {
        this.id = id;
        this.recipientOID = recipientOID;
        this.recipientName = recipientName;
        this.senderOID = senderOID;
        this.senderName = senderName;
        this.subject = subject;
        this.message = message;
        this.currencyType = currencyType;
        this.currencyAmount = currencyAmount;
        this.items = items;
        this.CoD = CoD;
        this.mailRead = false;
        this.mailArchive = false;
    }
    
    public void addItem(final OID itemOid, final int pos) {
        if (pos < this.items.size()) {
            this.items.add(pos, itemOid);
        }
    }
    
    public void itemTaken(final int pos) {
        this.items.set(pos, null);
    }
    
    @Override
    public String toString() {
        return "Mail subject: " + this.subject;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getRecipientName() {
        return this.recipientName;
    }
    
    public void setRecipientName(final String recipientName) {
        this.recipientName = recipientName;
    }
    
    public OID getRecipientOID() {
        return this.recipientOID;
    }
    
    public void setRecipientOID(final OID recipientOID) {
        this.recipientOID = recipientOID;
    }
    
    public String getSenderName() {
        return this.senderName;
    }
    
    public void setSenderName(final String senderName) {
        this.senderName = senderName;
    }
    
    public OID getSenderOID() {
        return this.senderOID;
    }
    
    public void setSenderOID(final OID senderOID) {
        this.senderOID = senderOID;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public int getCurrencyType() {
        return this.currencyType;
    }
    
    public void setCurrencyType(final int currencyType) {
        this.currencyType = currencyType;
    }
    
    public int getCurrencyAmount() {
        return this.currencyAmount;
    }
    
    public void setCurrencyAmount(final int currencyAmount) {
        this.currencyAmount = currencyAmount;
    }
    
    public ArrayList<OID> getItems() {
        return this.items;
    }
    
    public void setItems(final ArrayList<OID> items) {
        this.items = items;
    }
    
    public int getItemCategory() {
        return this.itemCategory;
    }
    
    public void setItemCategory(final int itemCategory) {
        this.itemCategory = itemCategory;
    }
    
    public boolean getCoD() {
        return this.CoD;
    }
    
    public void setCoD(final boolean CoD) {
        this.CoD = CoD;
    }
    
    public boolean getMailRead() {
        return this.mailRead;
    }
    
    public void setMailRead(final boolean mailRead) {
        this.mailRead = mailRead;
    }
    
    public boolean getMailArchive() {
        return this.mailRead;
    }
    
    public void setMailArchive(final boolean mailRead) {
        this.mailArchive = this.mailArchive;
    }
}
