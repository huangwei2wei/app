// 
// Decompiled by Procyon v0.5.30
// 

package atavism.persistence;

import java.util.HashMap;

public class PackageAliases
{
    private HashMap<String, String> aliasToPackage;
    private HashMap<String, String> packageToAlias;
    
    public PackageAliases() {
        this.aliasToPackage = new HashMap<String, String>();
        this.packageToAlias = new HashMap<String, String>();
    }
    
    public void addAlias(final String alias, final String packageName) {
        this.aliasToPackage.put(alias, packageName);
        this.packageToAlias.put(packageName, alias);
    }
    
    public String getAlias(final String packageName) {
        return this.packageToAlias.get(packageName);
    }
    
    public String getPackage(final String alias) {
        return this.aliasToPackage.get(alias);
    }
}
