// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

public class NodeQueue
{
    public int Capacity;
    private Node[] _heap;
    private int _size;
    
    public NodeQueue(final int n) {
        try {
            if (n <= 0) {
                throw new Exception("Capacity must be greater than 0");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.Capacity = n;
        this._heap = new Node[n + 1];
        this._size = 0;
    }
    
    public void Clear() {
        this._size = 0;
    }
    
    public Node Top() {
        return this._heap[0];
    }
    
    public Node Pop() {
        final Node result = this._heap[0];
        --this._size;
        this.TrickleDown(0, this._heap[this._size]);
        return result;
    }
    
    public void Push(final Node node) {
        ++this._size;
        this.BubbleUp(this._size - 1, node);
    }
    
    public void Modify(final Node node) {
        for (int i = 0; i < this._size; ++i) {
            if (this._heap[i] == node) {
                this.BubbleUp(i, node);
                return;
            }
        }
    }
    
    public Boolean Empty() {
        return this._size == 0;
    }
    
    private void BubbleUp(int i, final Node node) {
        for (int parent = (i - 1) / 2; i > 0 && this._heap[parent].Total > node.Total; i = parent, parent = (i - 1) / 2) {
            this._heap[i] = this._heap[parent];
        }
        this._heap[i] = node;
    }
    
    public void TrickleDown(int i, final Node node) {
        for (int child = i * 2 + 1; child < this._size; child = i * 2 + 1) {
            if (child + 1 < this._size && this._heap[child].Total > this._heap[child + 1].Total) {
                ++child;
            }
            this._heap[i] = this._heap[child];
            i = child;
        }
        this.BubbleUp(i, node);
    }
}
