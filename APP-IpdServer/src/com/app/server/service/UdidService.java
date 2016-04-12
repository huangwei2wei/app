package com.app.server.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
/**
 * @author mac
 */
public class UdidService implements Runnable {
    private Set<String> testUdidSet      = new HashSet<String>();
    private File        udidFile;
    private long        udidLastModified = 0;

    public UdidService() throws ConfigurationException {
        this.udidFile = new File(Thread.currentThread().getContextClassLoader().getResource("testudid.txt").getPath());
        this.udidLastModified = udidFile.lastModified();
        try {
            loadSources();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.setName("UdidService-Thread");
        thread.start();
    }

    public void run() {
        while (true) {
            if (isFileModified()) {
                try {
                    loadSources();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean isFileModified() {
        long t = this.udidFile.lastModified();
        if (t != this.udidLastModified) {
            this.udidLastModified = t;
            return true;
        }
        return false;
    }

    private void loadSources() throws IOException {
        Set<String> udidList = new HashSet<String>();
        FileReader read = null;
        BufferedReader br = null;
        try {
            read = new FileReader(Thread.currentThread().getContextClassLoader().getResource("testudid.txt").getPath());
            br = new BufferedReader(read);
            String row;
            while ((row = br.readLine()) != null) {
                udidList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                br.close();
            }
            if (null != read) {
                read.close();
            }
        }
        testUdidSet = udidList;
    }

    /**
     * 检查用户是否是测试用户
     * @param udid
     * @return
     */
    public boolean isTestUser(String udid) {
        return testUdidSet.contains(udid);
    }
}
