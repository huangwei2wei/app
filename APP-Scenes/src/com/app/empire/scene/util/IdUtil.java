package com.app.empire.scene.util;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参考https://github.com/twitter/snowflake/tree/snowflake-2010 id构成: 41位的时间前缀 +
 * 10位的节点标识（5位的datacenter和5位的workId） + 12位的sequence避免并发的数字(12位不够用时强制得到新的时间前缀)
 * 其中10位节点标识最大支持1024个节点 12位的sequence序列号，支持同一毫秒有4096个Id，这样每秒能支持4096000的Id生成能力
 * 
 * 注意：此方法对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间往回调整后，将会拒绝分配id。
 */
public class IdUtil {

	/**
	 * 基础时间点，可取最近的时间
	 */
	private long BASE_MILLI = 1446599291285L;

	/**
	 * 数据中心标识位
	 */
	private final static int DATA_CENTER_BITS = 5;

	/**
	 * 最大数据中心值表示的最大值
	 */
	private final static int MAX_DATA_CENTER_ID = -1 ^ (-1 << DATA_CENTER_BITS);

	/**
	 * 机器标识位
	 */
	private final static int WORK_ID_BITS = 5;

	/**
	 * 机器标识位最大值
	 */
	private final static int MAX_WORK_ID = -1 ^ (-1 << WORK_ID_BITS);

	/**
	 * 序列号位数
	 */
	private final static int SEQUENCE_BITS = 12;

	/**
	 * 序列号表示的最大数字
	 */
	private final static int MAX_SEQUENCE = -1 ^ (-1 << SEQUENCE_BITS);

	private final static int WORK_ID_SHIFT = SEQUENCE_BITS;

	private final static int DATA_CENTER_SHIFT = WORK_ID_BITS + SEQUENCE_BITS;

	private final static int MILLI_SHIFT = DATA_CENTER_BITS + WORK_ID_BITS + SEQUENCE_BITS;

	private int workId = 1;

	private int dataCenterId = 1;

	private long sequence = 0;

	private long lastTimestamp = 0;

	public IdUtil(Integer workId, Integer dataCenterId) {
		if (workId > MAX_WORK_ID || workId < 0) {
			throw new RuntimeException("workId is out of range");
		}
		if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
			throw new RuntimeException("dataCenterId is out of range");
		}
		this.workId = workId;
		this.dataCenterId = dataCenterId;
	}

	/**
	 * 获取下一个ID
	 * 
	 * @return
	 */
	public synchronized long nextId() {
		long milliseconds = System.currentTimeMillis();
		if (this.lastTimestamp == milliseconds) {
			// 同一个时间点，序列号递增
			this.sequence = (this.sequence + 1) & MAX_SEQUENCE;
			if (this.sequence == 0) {
				// 当同一个时间点的序列号递增道最大值时，强制等待到下一个时间周期
				milliseconds = tilNextMillis(this.lastTimestamp);
			}
		} else if (milliseconds < this.lastTimestamp) {
			throw new RuntimeException(
					String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
							this.lastTimestamp - milliseconds));
		} else {
			// 不同时间点，重置序列号为0
			this.sequence = 0;
		}
		this.lastTimestamp = milliseconds;
		long nextId = ((milliseconds - BASE_MILLI) << MILLI_SHIFT) | (this.dataCenterId << DATA_CENTER_SHIFT)
				| (this.workId << WORK_ID_SHIFT) | this.sequence;
		// Log.info(String.format("Create id user %d ms",
		// System.currentTimeMillis() - milliseconds));
		return nextId;
	}

	/**
	 * 获取下一个时钟周期时间点
	 * 
	 * @param lastTimestamp
	 * @return
	 */
	private long tilNextMillis(final long lastTimestamp) {
		long timestamp = System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}

	/**
	 * 根据nextId反向解析出时间
	 * 
	 * @param nextId
	 * @return
	 */
	public long getTimestampFromId(Long nextId) {
		return BASE_MILLI + (nextId >> MILLI_SHIFT);
	}

	public static void main(String[] args) throws InterruptedException {
		final AtomicInteger counter = new AtomicInteger(0);
		final ConcurrentHashMap<Long, String> set = new ConcurrentHashMap<Long, String>();
		final IdUtil uuid = new IdUtil(1, 1);
		int max = 100;
		final CountDownLatch countDownLatch = new CountDownLatch(max);
		final long start = System.currentTimeMillis();
		for (int i = 0; i < max; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while ((System.currentTimeMillis() - start) < 10000) {
						long id = uuid.nextId();
						if (set.get(id) != null) {
							System.out.println("conflic " + id);
						} else {
							set.put(id, "");
							counter.incrementAndGet();
						}
					}
					countDownLatch.countDown();
				}
			}).start();
		}
		countDownLatch.await();
		long end = System.currentTimeMillis();
		System.out.println("use time " + (end - start));
		System.out.println(counter.get() + "");
		System.out.println(set.keySet().size());

		/*
		 * 1446624225006 --> 104579605983243 1446624225006 --> 104579605983244
		 */
		System.out.println(uuid.getTimestampFromId(104579605983243L));
	}

}
