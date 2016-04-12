// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

public class IntArray
{
    public int[] Data;
    public int Size;
    public int Capacity;
    
    public IntArray() {
        this.Data = null;
        this.Size = 0;
        this.Capacity = 0;
    }
    
    public IntArray(final int n) {
        this.Resize(n);
    }
    
    public void Resize(final int n) {
        if (n > this.Capacity) {
            if (this.Capacity == 0) {
                this.Capacity = 10;
            }
            while (this.Capacity < n) {
                this.Capacity *= 2;
            }
            final int[] newData = new int[this.Capacity];
            if (this.Size > 0) {
                System.arraycopy(this.Data, 0, newData, 0, this.Size);
            }
            this.Data = newData;
        }
        this.Size = n;
    }
    
    public void Push(final int item) {
        this.Resize(this.Size + 1);
        this.Data[this.Size - 1] = item;
    }
    
    public int Pop() {
        if (this.Size > 0) {
            --this.Size;
        }
        return this.Data[this.Size];
    }
    
    public int get(final int i) {
        return this.Data[i];
    }
    
    public void set(final int i, final int value) {
        this.Data[i] = value;
    }
    
    public int[] ToArray() {
        return this.Data;
    }
}
