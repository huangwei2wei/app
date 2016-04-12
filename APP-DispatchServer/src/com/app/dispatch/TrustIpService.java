package com.app.dispatch;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * 类 <code>TrustIpService</code>执行可信ip段检查
 * 
 * @since JDK 1.6
 */
public class TrustIpService implements Runnable {
	private long trustIps[][];// 信任的ip 段
	private String type;
	private static final Logger log = Logger.getLogger(TrustIpService.class);
	private String trustipSingle = "trustip_single.txt";
	private String trustip = "trustip.txt";
	private long fileLastModified = 0;
	private File file;

	/**
	 * 实例化TrustIpService，根据参数类型实例化对应可信ip段文件
	 * 
	 * @param type
	 * @throws Exception
	 */
	public TrustIpService(String type) throws Exception {
		this.type = type;
		if (type.equals("singlesocket")) {
			load(trustipSingle);
			this.file = new File(Thread.currentThread().getContextClassLoader().getResource(trustipSingle).getPath());
			log.info("load trustip_single.txt");
		} else {
			load(trustip);
			this.file = new File(Thread.currentThread().getContextClassLoader().getResource(trustip).getPath());
			log.info("load trustip.txt");
		}
		this.fileLastModified = this.file.lastModified();
		start();
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setName("TrustIpService-Thread");
		thread.start();
	}

	protected boolean isFileModified() {
		long t = this.file.lastModified();
		if (t != this.fileLastModified) {
			this.fileLastModified = t;
			return true;
		}
		return false;
	}

	/**
	 * 读取文件，取出可信ip段
	 * 
	 * @param fname
	 * @throws Exception
	 */
	private void load(String fname) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(Thread.currentThread().getContextClassLoader().getResource(fname).getPath()));
		List<long[]> retList = new ArrayList<long[]>();
		while (true) {
			String line;
			if ((line = reader.readLine()) == null)
				break;
			if(line.startsWith("#"))
				continue;
			String secs[] = line.split("-");
			if (secs.length == 2) {
				long arr[] = new long[2];
				arr[0] = ipToLong(secs[0]);
				arr[1] = ipToLong(secs[1]);
				retList.add(arr);
			}
		}
		reader.close();
		trustIps = new long[retList.size()][2];
		retList.toArray(trustIps);
	}

	/**
	 * 重载可信ip文件
	 */
	public void reload() {
		try {
			if (type.equals("singelsocket"))
				load(trustipSingle);
			else
				load(trustip);
		} catch (Exception ex) {
			log.info(ex);
		}
	}

	/**
	 * 转换ip地址为int
	 * 
	 * @param s
	 * @return
	 */
	public static long ipToLong(String s) {
		String secs[] = s.split("\\.");
		return (Long.parseLong(secs[0]) << 24) + (Long.parseLong(secs[1]) << 16) + (Long.parseLong(secs[2]) << 8) + (Long.parseLong(secs[3]));
	}

	/**
	 * 转换ip地址为long
	 * 
	 * @param address
	 * @return
	 */
	public static long addressToLong(InetSocketAddress address) {
		byte bytes[] = address.getAddress().getAddress();
		long l = 0;
		for (byte b : bytes) {
			l <<= 8;
			l += b & 0xff;
		}
		return l;
	}

	/**
	 * 比较ip地址是否在可信ip段内
	 * 
	 * @param address
	 * @return
	 */
	public boolean isTrustIp(InetSocketAddress address) {
		if (address == null)
			return true;
		if (this.trustIps.length == 0)
			return true;
		long ip = addressToLong(address);
		for (int i = 0; i < this.trustIps.length; i++) {
			long start = this.trustIps[i][0];
			long end = this.trustIps[i][1];
			if ((ip >= start) && (ip <= end)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 增加可信ip段
	 * 
	 * @param begin
	 * @param end
	 */
	public void addTrustIp(int begin, int end) {
		long newTrustIps[][] = new long[trustIps.length + 1][2];
		System.arraycopy(trustIps, 0, newTrustIps, 0, trustIps.length);
		newTrustIps[trustIps.length][0] = begin;
		newTrustIps[trustIps.length][1] = end;
		trustIps = newTrustIps;
	}

	public static void main(String[] args) {
		try {
			System.out.println(addressToLong(new InetSocketAddress("0.0.0.255", 0)));
			System.out.println(ipToLong("0.0.0.255"));
			System.out.println(ipToLong("255.255.255.255"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (isFileModified()) {
					if (type.equals("singlesocket")) {
						load(trustipSingle);
					} else {
						load(trustip);
					}
				}
				Thread.sleep(10000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}