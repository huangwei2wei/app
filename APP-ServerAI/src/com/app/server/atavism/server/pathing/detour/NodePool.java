// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import com.app.server.atavism.server.pathing.recast.Helper;

public class NodePool
{
    public int MaxNodes;
    public int HashSize;
    private Node[] _nodes;
    private int[] _first;
    private int[] _next;
    private int _nodeCount;
    
    public NodePool(final int maxNodes, final int hashSize) {
        try {
            if (hashSize != Helper.NextPow2(hashSize)) {
                throw new Exception("Hash size must be a power of 2");
            }
            if (maxNodes <= 0) {
                throw new Exception("Max nodes must be greater than 0");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.MaxNodes = maxNodes;
        this.HashSize = hashSize;
        this._nodes = new Node[maxNodes];
        for (int i = 0; i < maxNodes; ++i) {
            this._nodes[i] = new Node();
        }
        this._next = new int[maxNodes];
        this._first = new int[hashSize];
        for (int i = 0; i < hashSize; ++i) {
            this._first[i] = Node.NullIdx;
        }
        for (int i = 0; i < maxNodes; ++i) {
            this._next[i] = Node.NullIdx;
        }
    }
    
    public void Clear() {
        this._nodeCount = 0;
        for (int i = 0; i < this.HashSize; ++i) {
            this._first[i] = Node.NullIdx;
        }
    }
    
    public Node GetNode(final long id) {
        final int bucket = (int)(Helper.HashRef(id) & this.HashSize - 1);
        int i = this._first[bucket];
        Node node = null;
        while (i != Node.NullIdx) {
            if (this._nodes[i].Id == id) {
                return this._nodes[i];
            }
            i = this._next[i];
        }
        if (this._nodeCount >= this.MaxNodes) {
            return null;
        }
        i = this._nodeCount;
        ++this._nodeCount;
        node = this._nodes[i];
        node.PIdx = 0L;
        node.Cost = 0.0f;
        node.Total = 0.0f;
        node.Id = id;
        node.Flags = 0L;
        this._next[i] = this._first[bucket];
        this._first[bucket] = i;
        return node;
    }
    
    public Node FindNode(final long id) {
        final int bucket = (int)(Helper.HashRef(id) & this.HashSize - 1);
        for (int i = this._first[bucket]; i != Node.NullIdx; i = this._next[i]) {
            if (this._nodes[i].Id == id) {
                return this._nodes[i];
            }
        }
        return null;
    }
    
    public long GetNodeIdx(final Node node) {
        if (node == null) {
            return 0L;
        }
        for (int i = 0; i < this._nodes.length; ++i) {
            if (node == this._nodes[i]) {
                return i + 1;
            }
        }
        return 0L;
    }
    
    public Node GetNodeAtIdx(final long idx) {
        if (idx <= 0L) {
            return null;
        }
        return this._nodes[(int)idx - 1];
    }
    
    public int GetFirst(final int bucket) {
        return this._first[bucket];
    }
    
    public int GetNext(final int i) {
        return this._next[i];
    }
}
