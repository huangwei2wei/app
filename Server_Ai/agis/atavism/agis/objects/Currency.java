// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;

public class Currency implements Serializable
{
    int id;
    String name;
    String icon;
    String description;
    int currentAmount;
    int maximumAmount;
    boolean external;
    boolean isSubCurrency;
    int subCurrencyID;
    int parentCurrencyID;
    private static final long serialVersionUID = 1L;
    
    public Currency() {
    }
    
    public Currency(final int id, final String name, final String icon, final String description, final int maximum, final boolean isSubCurrency) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.maximumAmount = maximum;
        this.currentAmount = 0;
        this.isSubCurrency = isSubCurrency;
    }
    
    public Currency(final Currency tmpl) {
        this.id = tmpl.id;
        this.name = tmpl.name;
        this.icon = tmpl.icon;
        this.description = tmpl.description;
        this.maximumAmount = tmpl.maximumAmount;
        this.currentAmount = 0;
    }
    
    public int getCurrencyID() {
        return this.id;
    }
    
    public void setCurrencyID(final int id) {
        this.id = id;
    }
    
    public String getCurrencyName() {
        return this.name;
    }
    
    public void setCurrencyName(final String name) {
        this.name = name;
    }
    
    public String getCurrencyIcon() {
        return this.icon;
    }
    
    public void setCurrencyIcon(final String icon) {
        this.icon = icon;
    }
    
    public String getCurrencyDescription() {
        return this.description;
    }
    
    public void setCurrencyDescription(final String description) {
        this.description = description;
    }
    
    public int getCurrencyMax() {
        return this.maximumAmount;
    }
    
    public void setCurrencyMax(final int maximumAmount) {
        this.maximumAmount = maximumAmount;
    }
    
    public int getCurrencyCurrent() {
        return this.currentAmount;
    }
    
    public void setCurrencyCurrent(final int currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public boolean getExternal() {
        return this.external;
    }
    
    public void setExternal(final boolean external) {
        this.external = external;
    }
    
    public boolean isSubCurrency() {
        return this.isSubCurrency;
    }
    
    public void isSubCurrency(final boolean isSubCurrency) {
        this.isSubCurrency = isSubCurrency;
    }
    
    public int getSubCurrency() {
        return this.subCurrencyID;
    }
    
    public void setSubCurrency(final int subCurrencyID) {
        this.subCurrencyID = subCurrencyID;
    }
    
    public int getParentCurrency() {
        return this.parentCurrencyID;
    }
    
    public void setParentCurrency(final int parentCurrencyID) {
        this.parentCurrencyID = parentCurrencyID;
    }
    
    public void alterCurrencyAmount(final int delta) {
        this.currentAmount += delta;
    }
}
