// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.util;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class DLock extends ReentrantLock {
	private static Logger log = Logger.getLogger("navmesh");
	String threadName;
	String lockName;
	static Map<DLock, Map<DLock, StackTraceElement[]>> lockOrderMap;
	static Map<Thread, LinkedList<DLock>> threadLocks;
	static int lockTimeoutMS;
	static ReentrantReadWriteLock rwLock;
	static ReentrantReadWriteLock.ReadLock rLock;
	static ReentrantReadWriteLock.WriteLock wLock;
	private static final long serialVersionUID = 1L;

	DLock(final String name) {
		this.threadName = null;
		this.lockName = "unknown";
		this.setName(name);
	}

	@Override
	public String toString() {
		return "[DLock lockName=" + this.getLockName() + ", hashCode=" + this.hashCode() + "]";
	}

	public void setName(final String name) {
		this.lockName = name;
	}

	public String getLockName() {
		return this.lockName;
	}

	@Override
	public boolean tryLock() {
		final boolean rv = super.tryLock();
		if (rv) {
			this.lock(false);
		}
		return rv;
	}

	@Override
	public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
		System.err.println("dlock.lock: called tryLock");
		throw new RuntimeException();
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		System.err.println("dlock.lock: called lockInterruptibly");
		throw new RuntimeException();
	}

	@Override
	public Condition newCondition() {
		log.warn("dlock.lock: called newCondition");
		return super.newCondition();
	}

	@Override
	public void lock() {
		this.lock(true);
	}

	public void lock(final boolean acquireLock) {
		final Thread currentThread = Thread.currentThread();
		log.debug("lock: name=" + this.lockName + ", thread=" + currentThread);
		boolean acquiredLock = false;
		DLock.rLock.lock();
		try {
			if (acquireLock) {
				try {
					acquiredLock = super.tryLock(DLock.lockTimeoutMS, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					System.err.println("dlock.lock: got exception: " + e);
					log.error("dlock.lock: got exception: " + e);
					System.exit(-1);
				}
			} else {
				acquiredLock = true;
			}
			if (acquiredLock) {
				synchronized (DLock.class) {
					LinkedList<DLock> locks = DLock.threadLocks.get(currentThread);
					if (locks == null) {
						log.info("lock: lockName=" + this.lockName + ", this thread has no locks yet, creating entry.  thread=" + currentThread);
						locks = new LinkedList<DLock>();
						DLock.threadLocks.put(currentThread, locks);
					}
					DLock lastLock = null;
					if (!locks.isEmpty()) {
						lastLock = locks.getLast();
					}
					locks.add(this);
					log.debug("lock: lockName=" + this.lockName + ", lastLock=" + lastLock);
					if (lastLock != null && lastLock != this) {
						Map<DLock, StackTraceElement[]> subMap = DLock.lockOrderMap.get(lastLock);
						if (subMap == null) {
							subMap = new HashMap<DLock, StackTraceElement[]>();
							DLock.lockOrderMap.put(lastLock, subMap);
						}
						if (subMap.get(this) == null) {
							final StackTraceElement[] dump = currentThread.getStackTrace();
							subMap.put(this, dump);
						}
					}
				}
			}
		} finally {
			DLock.rLock.unlock();
		}
		if (!acquiredLock) {
			this.throwException("acquire lock failed: forcing cycle detection");
			log.error("DLock.lock: thread with lock error is: " + currentThread);
			log.error("DLock.lock: dumping all threads currently holding locks");
			for (final Thread thread : DLock.threadLocks.keySet()) {
				final LinkedList<DLock> heldLocks = DLock.threadLocks.get(thread);
				for (final DLock heldLock : heldLocks) {
					log.error("DLock.lock: thread " + thread + ", is holding lock " + heldLock);
					if (heldLock == this) {
						log.error("DLock.lock: thread has lock in question, dumping stack trace:\n" + makeStackDumpString(thread.getStackTrace()));
					}
				}
			}
			log.error("DLock.lock: DONE dumping all threads currently held locks");
			log.error("DLock.lock: detecting cycles for lock in question: " + this);
			detectCycleHelper(this, new LinkedList<DLock>(), new HashSet<DLock>());
			log.error("DLock.lock: detecting all other cycles");
			detectCycle();
			log.error("deadlock cycle detection complete, exiting process");
			System.exit(-1);
		}
	}

	@Override
	public void unlock() {
		super.unlock();
		synchronized (DLock.class) {
			final Thread currentThread = Thread.currentThread();
			final LinkedList<DLock> locks = DLock.threadLocks.get(currentThread);
			if (locks == null) {
				this.throwException("unlock, but thread has no previous locks");
			}
			final DLock lastLock = locks.getLast();
			if (lastLock == null) {
				this.throwException("unlock, but thread has no previous locks");
			}
			if (lastLock == this) {
				locks.removeLast();
			} else {
				this.throwException("unlock, last lock did not match this lock");
			}
		}
	}

	void throwException(final String error) {
		log.error("DLock.throwException: lock in question is: " + this + ", thread=" + Thread.currentThread().getName() + ", msg=" + error);
	}

	public static Set<List<DLock>> detectCycle() {
		synchronized (DLock.class) {
			log.error("DLock.detectCycles: finding all child nodes");
			final Set<DLock> childNodes = getChildNodes();
			log.error("DLock.detectCycle: found " + childNodes.size() + " child nodes");
			final Set<DLock> topNodes = getTopNodes(childNodes);
			log.error("DLock.detectCycle: found " + topNodes.size() + " top nodes");
			final HashSet<DLock> cycleNodes = new HashSet<DLock>();
			final Set<List<DLock>> allCycles = new HashSet<List<DLock>>();
			for (final DLock node : topNodes) {
				log.error("DLock.detectCycle: doing depth first for top node " + node);
				final List<DLock> history = new LinkedList<DLock>();
				allCycles.addAll(detectCycleHelper(node, history, cycleNodes));
			}
			log.error("DLock.detectCycle: doing depth first for all child nodes");
			for (final DLock node : childNodes) {
				log.error("DLock.detectCycle: doing depth first for child node " + node);
				final List<DLock> history = new LinkedList<DLock>();
				final Set<List<DLock>> cycles = detectCycleHelper(node, history, cycleNodes);
				for (final List<DLock> cycle : cycles) {
					if (!isSubset(cycle, allCycles)) {
						allCycles.add(cycle);
					}
				}
			}
			log.error("DLock.detectCycle: printing out all detected cycles");
			for (final List<DLock> cycle2 : allCycles) {
				printCycle(cycle2);
			}
			log.error("DLock.detectCycle: done with printout");
			return allCycles;
		}
	}

	protected static boolean isSubset(final List<DLock> set, final Set<List<DLock>> allSets) {
		for (final List<DLock> testSet : allSets) {
			if (isSubset(set, testSet)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean isSubset(final List<DLock> subset, final List<DLock> superset) {
		for (final DLock element : subset) {
			if (!superset.contains(element)) {
				return false;
			}
		}
		return true;
	}

	protected static Set<DLock> getChildNodes() {
		final Set<DLock> childNodes = new HashSet<DLock>();
		for (final Map<DLock, StackTraceElement[]> subMap : DLock.lockOrderMap.values()) {
			childNodes.addAll(subMap.keySet());
		}
		return childNodes;
	}

	protected static Set<DLock> getTopNodes(final Set<DLock> childNodes) {
		final Set<DLock> topNodes = new HashSet<DLock>();
		for (final DLock node : DLock.lockOrderMap.keySet()) {
			if (!childNodes.contains(node)) {
				topNodes.add(node);
			}
		}
		return topNodes;
	}

	protected static Set<List<DLock>> detectCycleHelper(final DLock node, final List<DLock> history, final HashSet<DLock> cycleNodes) {
		log.debug("DLock.detectCyleHelper: considering node " + node + ", numCycleNodes=" + cycleNodes.size());
		if (history.contains(node)) {

			log.debug("DLock.detectCycleHelper: node " + node + " already in history, cycle detected");
			history.add(node);
			final Set<List<DLock>> cycle = new HashSet<List<DLock>>();
			cycle.add(history);
			cycleNodes.add(node);
			return cycle;
		}
		history.add(node);
		final Map<DLock, StackTraceElement[]> subMap = DLock.lockOrderMap.get(node);
		if (subMap == null) {
			log.debug("DLock.detectCycleHelper: node is leaf, returning");
			return new HashSet<List<DLock>>();
		}
		final Set<DLock> childSet = subMap.keySet();
		if (childSet == null || childSet.isEmpty()) {
			log.debug("DLock.detectCycleHelper: node is leaf, returning");
			return new HashSet<List<DLock>>();
		}
		final Set<List<DLock>> cycles = new HashSet<List<DLock>>();
		for (final DLock child : childSet) {
			if (cycleNodes.contains(child)) {
				log.debug("DLock.detectCycleHelper: child already causes cycle, skipping - currentNode=" + node + ", child=" + child);
			} else {
				log.debug("DLock.detectCycleHelper: currentNode=" + node + ", decending, child=" + child + ", numChildren=" + childSet.size());
				final List<DLock> historyCopy = new LinkedList<DLock>(history);
				final Set<List<DLock>> childCycles = detectCycleHelper(child, historyCopy, cycleNodes);
				cycles.addAll(childCycles);
			}
		}
		return cycles;
	}

	protected static void stackDump(final List<DLock> nodes) {
		if (nodes == null) {
			System.err.println("ERROR: DLock.stackDump: nodes is null");
			return;
		}
		if (nodes.isEmpty()) {
			System.err.println("ERROR: DLock.stackDump: nodes list is empty");
			return;
		}
		System.err.println("Found Cycle, dumping history");
		final Iterator<DLock> iter = nodes.iterator();
		DLock prev = null;
		DLock cur = null;
		prev = iter.next();
		while (iter.hasNext()) {
			cur = iter.next();
			final StackTraceElement[] stackArray = DLock.lockOrderMap.get(prev).get(cur);
			String stackTrace = "";
			for (int i = 0; i < stackArray.length; ++i) {
				stackTrace = stackTrace + "  stack" + i + "=" + stackArray[i].toString() + "\n";
			}
			System.err.println("ERROR: DLock.stackDump: had lock " + prev.getLockName() + " when locked " + cur.getLockName() + ", dump:\n" + stackTrace);
			prev = cur;
		}
	}

	protected static void printCycle(final List<DLock> cycle) {
		String s = "Cycle: ";
		DLock prev = null;
		for (final DLock node : cycle) {
			s = s + "\nlock=" + node;
			if (prev != null) {
				s += ", stackdump:\n";
				final Map<DLock, StackTraceElement[]> subMap = DLock.lockOrderMap.get(prev);
				final StackTraceElement[] trace = subMap.get(node);
				for (int i = 0; i < trace.length; ++i) {
					s = s + "  stack" + i + "=" + trace[i].toString() + "\n";
				}
			}
			prev = node;
		}
		log.error("DLock.printCycle: " + s);
	}

	public static String makeStackDumpString(final StackTraceElement[] stackArray) {
		String stackTrace = "";
		for (int i = 0; i < stackArray.length; ++i) {
			stackTrace = stackTrace + "  stack" + i + "=" + stackArray[i].toString() + "\n";
		}
		return stackTrace;
	}

	public static void main(final String[] args) {
		final DLock a = new DLock("A");
		final DLock b = new DLock("B");
		a.lock();
		b.lock();
		a.lock();
		a.unlock();
		b.unlock();
		a.unlock();
		System.out.println("detecting cycles..");
		detectCycle();
		System.out.println("done");
	}

	static {
		DLock.lockOrderMap = new HashMap<DLock, Map<DLock, StackTraceElement[]>>();
		DLock.threadLocks = new HashMap<Thread, LinkedList<DLock>>();
		DLock.lockTimeoutMS = 30000;
		DLock.rwLock = new ReentrantReadWriteLock();
		DLock.rLock = DLock.rwLock.readLock();
		DLock.wLock = DLock.rwLock.writeLock();
	}
}
