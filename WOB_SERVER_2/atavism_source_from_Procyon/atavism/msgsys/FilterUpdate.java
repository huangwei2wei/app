// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FilterUpdate
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
    
    public static class Instruction
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
    }
}
