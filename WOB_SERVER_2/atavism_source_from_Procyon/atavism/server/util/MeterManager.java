// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Iterator;
import java.io.IOException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Vector;

public class MeterManager
{
    protected static MeterManager instance;
    protected short timerIdCounter;
    protected long startTime;
    float microsecondsPerTick;
    protected Vector<MeterEvent> eventTrace;
    protected HashMap<String, TimingMeter> metersByTitle;
    protected HashMap<Short, TimingMeter> metersById;
    protected static boolean dumpEventLog;
    public static boolean Collecting;
    public static int DontDisplayUsecs;
    public static short ekEnter;
    public static short ekExit;
    
    protected static long CaptureCurrentTime() {
        return System.nanoTime();
    }
    
    protected String OptionValue(final String name, final HashMap<String, String> options) {
        final String value;
        if ((value = options.get(name)) != null) {
            return value;
        }
        return "";
    }
    
    protected boolean BooleanOption(final String name, final HashMap<String, String> options) {
        final String value = this.OptionValue(name, options);
        return value != "" && value != "false";
    }
    
    protected int IntOption(final String name, final HashMap<String, String> options) {
        final String value = this.OptionValue(name, options);
        return (value == "") ? 0 : Integer.parseInt(value);
    }
    
    protected static void BarfOnBadChars(final String name, final String nameDescription) throws Exception {
        if (name.indexOf("\n") >= 0) {
            throw new Exception(String.format("Carriage returns are not allowed in %1$s", nameDescription));
        }
        if (name.indexOf(",") >= 0) {
            throw new Exception(String.format("Commas are not allowed in %1$s", nameDescription));
        }
    }
    
    protected MeterManager() {
        this.timerIdCounter = 1;
        this.eventTrace = new Vector<MeterEvent>();
        this.metersByTitle = new HashMap<String, TimingMeter>();
        this.metersById = new HashMap<Short, TimingMeter>();
        this.startTime = CaptureCurrentTime();
        this.microsecondsPerTick = 0.001f;
        MeterManager.instance = this;
    }
    
    protected TimingMeter GetMeterById(final int id) {
        final TimingMeter meter = this.metersById.get(id);
        assert meter != null : String.format("Meter for id %1$d is not in the index", id);
        return meter;
    }
    
    protected void SaveToFileInternal(final String pathname) {
        try {
            final FileWriter writer = new FileWriter(pathname);
            writer.write(String.format("MeterCount=%1$d\n", this.metersById.size()));
            for (final TimingMeter meter : MeterManager.instance.metersById.values()) {
                writer.write(String.format("%1$s,%2$s,%3$d\n", meter.title, meter.category, meter.meterId));
            }
        }
        catch (IOException ex) {}
    }
    
    protected String IndentCount(int count) {
        if (count > 20) {
            count = 20;
        }
        final String s = "|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-";
        return s.substring(0, 2 * count);
    }
    
    protected long ToMicroseconds(final long ticks) {
        return (long)(ticks * this.microsecondsPerTick);
    }
    
    protected void DumpEventLogInternal() {
        final String p = "../MeterEvents.txt";
        try {
            final FileWriter writer = new FileWriter(p);
            writer.write(String.format("Dumping meter event log; units are usecs\r\n", new Object[0]));
            int indent = 0;
            final Vector<MeterStackEntry> meterStack = new Vector<MeterStackEntry>();
            for (int i = 0; i < this.eventTrace.size(); ++i) {
                final MeterEvent me = this.eventTrace.get(i);
                final TimingMeter meter = this.GetMeterById(me.meterId);
                final short kind = me.eventKind;
                final long t = me.eventTime;
                if (kind == MeterManager.ekEnter) {
                    ++indent;
                    writer.write(String.format("%112d %2s%3s %4s.%5s\r\n", this.ToMicroseconds(t - this.startTime), this.IndentCount(indent), "Enter", meter.category, meter.title));
                    meterStack.add(new MeterStackEntry(meter, t));
                }
                else {
                    assert meterStack.size() > 0 : "Meter stack is empty during ekExit";
                    final MeterStackEntry s = meterStack.get(meterStack.size() - 1);
                    assert s.meter == meter;
                    writer.write(String.format("%112d %2s%3s %4s.%5s\r\n", this.ToMicroseconds(t - s.eventTime), this.IndentCount(indent), "Exit ", meter.category, meter.title));
                    --indent;
                    meterStack.remove(meterStack.size() - 1);
                }
            }
            writer.close();
        }
        catch (Exception ex) {}
    }
    
    protected void GenerateReport(final FileWriter writer, final int start, final HashMap<String, String> options) {
        if (MeterManager.dumpEventLog) {
            DumpEventLog();
        }
        try {
            for (final TimingMeter meter : MeterManager.instance.metersById.values()) {
                meter.stackDepth = 0;
                meter.addedTime = 0L;
            }
            final Vector<MeterStackEntry> meterStack = new Vector<MeterStackEntry>();
            int indent = 0;
            for (int i = 0; i < this.eventTrace.size(); ++i) {
                final MeterEvent me = this.eventTrace.get(i);
                final TimingMeter meter2 = this.GetMeterById(me.meterId);
                final short kind = me.eventKind;
                final long t = me.eventTime;
                if (kind == MeterManager.ekEnter) {
                    if (meter2.accumulate && meter2.stackDepth == 0) {
                        meter2.addedTime = 0L;
                    }
                    if (i >= start && (!meter2.accumulate || meter2.stackDepth == 0)) {
                        if (this.eventTrace.size() > i + 1 && this.eventTrace.get(i + 1).meterId == me.meterId && this.eventTrace.get(i + 1).eventKind == MeterManager.ekExit && this.ToMicroseconds(this.eventTrace.get(i + 1).eventTime - t) < MeterManager.DontDisplayUsecs) {
                            ++i;
                            continue;
                        }
                        writer.write(String.format("%112d %2s%3s %4s.%5s\r\n", this.ToMicroseconds(t - this.startTime), this.IndentCount(indent), "Enter", meter2.accumulate ? "*" : " ", meter2.category, meter2.title));
                        if (!meter2.accumulate) {
                            ++indent;
                        }
                    }
                    final TimingMeter timingMeter = meter2;
                    ++timingMeter.stackDepth;
                    meterStack.add(new MeterStackEntry(meter2, t));
                }
                else if (kind == MeterManager.ekExit) {
                    assert meterStack.size() > 0 : "Meter stack is empty during ekExit";
                    final MeterStackEntry s = meterStack.get(meterStack.size() - 1);
                    final TimingMeter timingMeter2 = meter2;
                    --timingMeter2.stackDepth;
                    assert s.meter == meter2;
                    if (meter2.stackDepth > 0 && meter2.accumulate) {
                        final TimingMeter timingMeter3 = meter2;
                        timingMeter3.addedTime += t - s.eventTime;
                    }
                    else if (i >= start) {
                        if (!meter2.accumulate) {
                            --indent;
                        }
                        writer.write(String.format("%112d %2s%3s %4s.%5s\r\n", this.ToMicroseconds(meter2.accumulate ? meter2.addedTime : (t - s.eventTime)), this.IndentCount(indent), "Exit ", meter2.accumulate ? "*" : " ", meter2.category, meter2.title));
                    }
                    meterStack.remove(meterStack.size() - 1);
                }
            }
        }
        catch (Exception ex) {}
    }
    
    public static void Init() {
        if (MeterManager.instance == null) {
            MeterManager.instance = new MeterManager();
        }
    }
    
    public static void EnableCategory(final String categoryName, final boolean enable) {
        Init();
        for (final TimingMeter meter : MeterManager.instance.metersById.values()) {
            if (meter.category == categoryName) {
                meter.enabled = enable;
            }
        }
    }
    
    public static TimingMeter GetMeter(final String title, final String category) {
        Init();
        TimingMeter meter;
        if ((meter = MeterManager.instance.metersByTitle.get(title)) != null) {
            return meter;
        }
        try {
            BarfOnBadChars(title, "TimingMeter title");
            BarfOnBadChars(title, "TimingMeter category");
        }
        catch (Exception e) {
            return null;
        }
        final MeterManager instance = MeterManager.instance;
        final short timerIdCounter = instance.timerIdCounter;
        instance.timerIdCounter = (short)(timerIdCounter + 1);
        final short id = timerIdCounter;
        meter = new TimingMeter(title, category, id);
        MeterManager.instance.metersByTitle.put(title, meter);
        MeterManager.instance.metersById.put(id, meter);
        return meter;
    }
    
    public static TimingMeter GetMeter(final String title, final String category, final boolean accumulate) {
        final TimingMeter meter = GetMeter(title, category);
        meter.accumulate = true;
        return meter;
    }
    
    public static int AddEvent(final TimingMeter meter, final short eventKind) {
        final long time = CaptureCurrentTime();
        MeterManager.instance.eventTrace.add(new MeterEvent(meter.meterId, eventKind, time));
        return MeterManager.instance.eventTrace.size();
    }
    
    public static void ClearEvents() {
        Init();
        MeterManager.instance.eventTrace.clear();
    }
    
    public static void SaveToFile(final String pathname) {
        MeterManager.instance.SaveToFileInternal(pathname);
    }
    
    public static long StartTime() {
        Init();
        return MeterManager.instance.startTime;
    }
    
    public static void Report(final String title) {
        Report(title, null, 0, "");
    }
    
    public static void Report(final String title, FileWriter writer, final int start, final String optionsString) {
        boolean opened = false;
        if (writer == null) {
            final String p = "../MeterLog.txt";
            try {
                writer = new FileWriter(p, true);
                writer.write(String.format("\r\n\r\n\r\nStarting meter report for %1s; starting event %2d; units are usecs\r\n", title, start));
                opened = true;
            }
            catch (IOException e) {
                return;
            }
        }
        MeterManager.instance.GenerateReport(writer, start, null);
        if (opened) {
            try {
                writer.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static void DumpEventLog() {
        Init();
        MeterManager.instance.DumpEventLogInternal();
    }
    
    static {
        MeterManager.instance = null;
        MeterManager.dumpEventLog = false;
        MeterManager.DontDisplayUsecs = 3;
        MeterManager.ekEnter = 1;
        MeterManager.ekExit = 2;
    }
    
    protected static class MeterEvent
    {
        public short meterId;
        public short eventKind;
        public long eventTime;
        
        public MeterEvent(final short meterId, final short eventKind, final long eventTime) {
            this.meterId = meterId;
            this.eventKind = eventKind;
            this.eventTime = eventTime;
        }
    }
    
    protected static class MeterStackEntry
    {
        public TimingMeter meter;
        public long eventTime;
        
        public MeterStackEntry(final TimingMeter meter, final long eventTime) {
            this.meter = meter;
            this.eventTime = eventTime;
        }
    }
}
