// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import java.util.Random;

public class Dice
{
    private int sides;
    private Random random;
    public static Dice StdDice;
    
    static {
        Dice.StdDice = new Dice(6);
    }
    
    public Dice(final int sides) {
        this.sides = 0;
        this.random = new Random();
        this.sides = sides;
    }
    
    public synchronized int roll(int numTimes) {
        int total = 0;
        while (numTimes > 0) {
            total = total + this.random.nextInt(this.sides) + 1;
            --numTimes;
        }
        return total;
    }
}
