package com.app.empire.gameaccount.service.factory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.app.empire.gameaccount.exception.ParseException;
import com.app.net.IService;
public class ClientListManager implements Runnable, IService {
    private File                                    file;
    private long                                    lastModified;
    private ConcurrentHashMap<String, ClientDetail> sources = new ConcurrentHashMap<String, ClientDetail>();

    public ClientListManager(File file) throws Exception {
        this.file = file;
        this.lastModified = file.lastModified();
        loadSources();
    }

    public void start() {
        Thread thread = new Thread();
        thread.setName("ClientDetailMonitor");
        thread.start();
    }

    private void loadSources() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(this.file));
        SourceDef[] preSources = createSources(reader);
        for (int i = 0; i < preSources.length; ++i) {
            ClientDetail source = (ClientDetail) this.sources.get(preSources[i].id);
            if (source != null) {
                source.setPassWord(preSources[i].password);
                source.setAddress(preSources[i].address);
            } else {
                source = new ClientDetail(preSources[i].id, preSources[i].password, preSources[i].address);
                this.sources.put(source.getId(), source);
            }
        }
    }

    public ClientDetail getClientDetail(String id) {
        return ((ClientDetail) this.sources.get(id));
    }

    private SourceDef[] createSources(BufferedReader reader) throws IOException {
        List<SourceDef> l = new ArrayList<SourceDef>(100);
        String s = null;
        while ((s = reader.readLine()) != null) {
            if (s.startsWith("#")) continue;
            try {
                SourceDef source = createSource(s);
                l.add(source);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        SourceDef[] source = new SourceDef[l.size()];
        l.toArray(source);
        return source;
    }

    private SourceDef createSource(String s) throws ParseException {
        String[] ss = s.split(";");
        if (ss.length != 3) throw new ParseException();
        if (!(checkServiceId(ss[0]))) throw new ParseException();
        if (!(checkPassword(ss[1]))) throw new ParseException();
        int address = strToIP(ss[2]);
        SourceDef ret = new SourceDef();
        ret.id = ss[0];
        ret.password = ss[1];
        ret.address = address;
        return ret;
    }

    private boolean checkServiceId(String s) {
        return (s.length() != 0);
    }

    private boolean checkPassword(String s) {
        return (s.length() != 0);
    }

    public int strToIP(String s) {
        String[] secs = s.split("\\.");
        return (Integer.parseInt(secs[0]) << 24 & 0xFF000000 | Integer.parseInt(secs[1]) << 16 & 0xFF0000 | Integer.parseInt(secs[2]) << 8 & 0xFF00 | Integer.parseInt(secs[3]) & 0xFF);
    }

    public void run() {
        while (true) {
            if (isFileModified()) try {
                loadSources();
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
        long t = this.file.lastModified();
        if (t != this.lastModified) {
            this.lastModified = t;
            return true;
        }
        return false;
    }
    static class SourceDef {
        String id;
        String password;
        int    address;
    }
}
