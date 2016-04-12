// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.InputStream;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.beans.PersistenceDelegate;
import atavism.server.util.Log;
import java.beans.ExceptionListener;
import java.io.OutputStream;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.beans.Expression;
import java.beans.Encoder;
import java.beans.DefaultPersistenceDelegate;

public class AOObjectPersistenceDelegate extends DefaultPersistenceDelegate
{
    @Override
    protected void initialize(final Class type, final Object oldInstance, final Object newInstance, final Encoder out) {
        System.out.println("AOObjectPersistenceDelegate: start persisting obj: " + oldInstance);
        super.initialize(type, oldInstance, newInstance, out);
        System.out.println("AOObjectPersistenceDelegate: super.initialize returned, obj: " + oldInstance);
    }
    
    @Override
    protected Expression instantiate(final Object oldInstance, final Encoder out) {
        final ObjectType objectType = (ObjectType)oldInstance;
        System.out.println("instantiate: " + objectType);
        System.out.println("instantiate: " + objectType.getTypeId());
        return new Expression(ObjectType.class, "getObjectType", new Object[] { objectType.getTypeId() });
    }
    
    @Override
    protected boolean mutatesTo(final Object oldInstance, final Object newInstance) {
        return oldInstance == newInstance;
    }
    
    public static void main(final String[] args) {
        ObjectType.intern((short)33, "thirty");
        final Two two = new Two();
        two.setO1(ObjectType.intern((short)2, "two"));
        two.setO2(ObjectType.intern((short)2, "two"));
        two.setType(ObjectTypes.player);
        final Object object = two;
        final ByteArrayOutputStream xml = new ByteArrayOutputStream(1000);
        final XMLEncoder encoder = new XMLEncoder(xml);
        encoder.setExceptionListener(new ExceptionListener() {
            @Override
            public void exceptionThrown(final Exception exception) {
                Log.exception("AOObjectPersistenceDelegate.main caught exception setting encoder exception listener", exception);
            }
        });
        encoder.setPersistenceDelegate(ObjectType.class, new AOObjectPersistenceDelegate());
        encoder.writeObject(object);
        encoder.close();
        System.out.println(xml.toString());
        final XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(xml.toByteArray()));
        final Two x2 = (Two)d.readObject();
        System.out.println("decode1: " + x2.getO1());
        System.out.println("decode2: " + x2.getO2());
    }
    
    public static class One
    {
        ObjectType type;
        
        public One() {
            this.type = ObjectTypes.unknown;
        }
        
        public ObjectType getType() {
            return this.type;
        }
        
        public void setType(final ObjectType t) {
            this.type = t;
        }
    }
    
    public static class Two extends One
    {
        ObjectType o1;
        ObjectType o2;
        
        public ObjectType getO1() {
            return this.o1;
        }
        
        public ObjectType getO2() {
            return this.o2;
        }
        
        public void setO1(final ObjectType o) {
            this.o1 = o;
        }
        
        public void setO2(final ObjectType o) {
            this.o2 = o;
        }
    }
}
