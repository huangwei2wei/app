// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import atavism.server.marshalling.Marshallable;

public class FilterUpdate implements Marshallable
{
    public static final int OP_SET = 1;
    public static final int OP_ADD = 2;
    public static final int OP_REMOVE = 3;
    protected List<Instruction> instructions;
    
    public FilterUpdate() {
        this.instructions = new LinkedList<Instruction>();
        this.instructions = new LinkedList<Instruction>();
    }
    
    public FilterUpdate(final int capacity) {
        this.instructions = new LinkedList<Instruction>();
        this.instructions = new ArrayList<Instruction>(capacity);
    }
    
    public void setField(final int fieldId, final Object value) {
        this.instructions.add(new Instruction(1, fieldId, value));
    }
    
    public void addFieldValue(final int fieldId, final Object value) {
        this.instructions.add(new Instruction(2, fieldId, value));
    }
    
    public void removeFieldValue(final int fieldId, final Object value) {
        this.instructions.add(new Instruction(3, fieldId, value));
    }
    
    public List<Instruction> getInstructions() {
        return this.instructions;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.instructions != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.instructions != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.instructions);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.instructions = (List<Instruction>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
    
    public static class Instruction implements Marshallable
    {
        public int opCode;
        public int fieldId;
        public Object value;
        
        public Instruction() {
        }
        
        public Instruction(final int op, final int fieldId, final Object value) {
            this.opCode = op;
            this.fieldId = fieldId;
            this.value = value;
        }
        
        public void marshalObject(final AOByteBuffer buf) {
            byte flag_bits = 0;
            if (this.value != null) {
                flag_bits = 1;
            }
            buf.putByte(flag_bits);
            buf.putInt(this.opCode);
            buf.putInt(this.fieldId);
            if (this.value != null) {
                MarshallingRuntime.marshalObject(buf, this.value);
            }
        }
        
        public Object unmarshalObject(final AOByteBuffer buf) {
            final byte flag_bits0 = buf.getByte();
            this.opCode = buf.getInt();
            this.fieldId = buf.getInt();
            if ((flag_bits0 & 0x1) != 0x0) {
                this.value = MarshallingRuntime.unmarshalObject(buf);
            }
            return this;
        }
    }
}
