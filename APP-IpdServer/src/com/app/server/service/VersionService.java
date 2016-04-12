package com.app.server.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
/**
 * @author Administrator
 */
public class VersionService implements Runnable {
    private Set<String> testVersionSet      = new HashSet<String>();
    private File        versionFile;
    private long        versionLastModified = 0;
    private Set<String> testChannelSet      = new HashSet<String>();
    private File        channelFile;
    private long        channelLastModified = 0;
    private File        configFile;
    private long        configLastModified;

    public VersionService() throws ConfigurationException {
        this.versionFile = new File(Thread.currentThread().getContextClassLoader().getResource("testversion.txt").getPath());
        this.versionLastModified = versionFile.lastModified();
        this.channelFile = new File(Thread.currentThread().getContextClassLoader().getResource("testchannel.txt").getPath());
        this.channelLastModified = channelFile.lastModified();
        try {
            loadSources();
            loadSources2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        configFile = new File(Thread.currentThread().getContextClassLoader().getResource("config.properties").getPath());
        this.configLastModified = configFile.lastModified();
        start();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.setName("VersionService-Thread");
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
            if (isFileModified2()) {
                try {
                    loadSources2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                long t = this.configFile.lastModified();
                if (t != this.configLastModified) {
                    this.configLastModified = t;
                    ServiceManager.getManager().loadConfig();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean isFileModified() {
        long t = this.versionFile.lastModified();
        if (t != this.versionLastModified) {
            this.versionLastModified = t;
            return true;
        }
        return false;
    }
    
    protected boolean isFileModified2() {
        long t = this.channelFile.lastModified();
        if (t != this.channelLastModified) {
            this.channelLastModified = t;
            return true;
        }
        return false;
    }

    private void loadSources() throws IOException {
        Set<String> versionList = new HashSet<String>();
        FileReader read = null;
        BufferedReader br = null;
        try {
            read = new FileReader(Thread.currentThread().getContextClassLoader().getResource("testversion.txt").getPath());
            br = new BufferedReader(read);
            String row;
            while ((row = br.readLine()) != null) {
                versionList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br)
                br.close();
            if (null != read)
                read.close();
        }
        testVersionSet = versionList;
    }
    
    private void loadSources2() throws IOException {
        Set<String> channelList = new HashSet<String>();
        FileReader read = null;
        BufferedReader br = null;
        try {
            read = new FileReader(Thread.currentThread().getContextClassLoader().getResource("testchannel.txt").getPath());
            br = new BufferedReader(read);
            String row;
            while ((row = br.readLine()) != null) {
                channelList.add(row);
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
        testChannelSet = channelList;
    }

    /**
     * 检查版本是否是测试版本
     * 
     * @param udid
     * @return
     */
    public boolean isTestVersion(String version) {
        return testVersionSet.contains(version);
    }
    
    /**
     * 检查渠道是否是测试渠道
     * @param udid
     * @return
     */
    public boolean isTestChannel(String channel) {
        return testChannelSet.contains(channel);
    }
}
