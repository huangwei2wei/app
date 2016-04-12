// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.HashMap;
import atavism.server.plugins.JukeboxWebPlugin;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;

public class JukeboxWebEngine extends HttpServlet
{
    private ArrayList<String> nameList;
    private ArrayList<String> typeList;
    private ArrayList<String> urlList;
    private ArrayList<String> costList;
    private ArrayList<String> descriptionList;
    private static final long serialVersionUID = 1L;
    
    public void init(final ServletConfig config) throws ServletException {
        this.nameList = new ArrayList<String>();
        this.typeList = new ArrayList<String>();
        this.urlList = new ArrayList<String>();
        this.costList = new ArrayList<String>();
        this.descriptionList = new ArrayList<String>();
        final JukeboxWebEngineThread jukeboxWebEngineThread = new JukeboxWebEngineThread();
        new Thread(jukeboxWebEngineThread).start();
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        res.setHeader("pragma", "no-cache");
        final String playerOid = req.getParameter("poid");
        final PrintWriter out = res.getWriter();
        if (playerOid == null || playerOid.length() == 0) {
            out.print("<HTML><HEAD><TITLE>Jukebox Media Manager</TITLE></HEAD>");
            out.print("<BODY><H3>Jukebox Media Control:</H3><TABLE border=\"1\">");
            out.print("<TR><TH>NAME</TH><TH>TYPE</TH><TH>URL</TH><TH>COST</TH><TH>DESCRIPTION</TH></TR>");
            for (int i = 0; i < this.nameList.size(); ++i) {
                out.print("<TR>");
                out.print("<TD>" + this.nameList.get(i) + "</TD>");
                out.print("<TD>" + this.typeList.get(i) + "</TD>");
                out.print("<TD>" + this.urlList.get(i) + "</TD>");
                out.print("<TD>" + this.costList.get(i) + "</TD>");
                out.print("<TD>" + this.descriptionList.get(i) + "</TD>");
                out.print("</TR>");
            }
            out.print("</TABLE><HR><FORM METHOD=POST>");
            out.print("<TABLE>");
            out.print("<TR><TD>name:</TD><TD><INPUT TYPE=TEXT NAME=name></TD></TR>");
            out.print("<TR><TD>type:</TD><TD><INPUT TYPE=TEXT NAME=type></TD></TR>");
            out.print("<TR><TD>url:</TD><TD><INPUT TYPE=TEXT NAME=url></TD></TR>");
            out.print("<TR><TD>cost:</TD><TD><INPUT TYPE=TEXT NAME=cost></TD></TR>");
            out.print("<TR><TD>description:</TD><TD><INPUT TYPE=TEXT NAME=description></TD></TR>");
            out.print("<TR><TD></TD></TR>");
            out.print("<TR><TD align=\"center\" colspan=\"2\">");
            out.print("<INPUT TYPE=SUBMIT NAME=action VALUE=add>");
            out.print("<INPUT TYPE=SUBMIT NAME=action VALUE=delete>");
            out.print("<INPUT TYPE=SUBMIT NAME=action VALUE=get>");
            out.print("</TD></TR>");
            out.print("</TABLE>");
            out.print("</FORM></BODY></HTML>");
            out.close();
        }
        else {
            final int funds = this.getMoney(playerOid);
            out.print("<HTML><HEAD><TITLE>Jukebox Funds Manager</TITLE></HEAD>");
            out.print("<BODY><H3>Jukebox Funds Control:</H3>");
            out.print("Player OID: " + playerOid + "<BR>");
            out.print("Current Funds: $" + funds / 100);
            if (funds % 100 < 10) {
                out.print(".0" + funds % 100 + "<BR>");
            }
            else {
                out.print("." + funds % 100 + "<BR>");
            }
            out.print("<FORM METHOD=POST>");
            out.print("<INPUT TYPE=TEXT NAME=money><BR>");
            out.print("<INPUT TYPE=SUBMIT NAME=action VALUE=deposit><BR>");
            out.print("<INPUT TYPE=HIDDEN NAME=poid VALUE=" + playerOid + "><BR>");
            out.print("</FORM>");
            out.print("</BODY></HTML>");
            out.close();
        }
    }
    
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        final String name = req.getParameter("name");
        final String type = req.getParameter("type");
        final String url = req.getParameter("url");
        final String cost = req.getParameter("cost");
        String description = req.getParameter("description");
        final String poid = req.getParameter("poid");
        final String money = req.getParameter("money");
        String msg = "";
        if (poid == null || poid.length() == 0) {
            if (name.length() == 0 && !req.getParameter("action").equals("get")) {
                res.sendError(400, "No name specified.");
                return;
            }
            if (req.getParameter("action").equals("add")) {
                if (type.length() == 0) {
                    res.sendError(400, "Type must be stream or audio.");
                    return;
                }
                if (url.length() == 0) {
                    res.sendError(400, "No url specified.");
                    return;
                }
                if (cost.length() == 0) {
                    res.sendError(400, "No cost specified.");
                    return;
                }
                if (description.length() == 0) {
                    description = "n/a";
                }
                if (!this.addTrack(name, type, url, cost, description)) {
                    res.sendError(400, "" + name + " is already in the list.");
                    return;
                }
                msg = "" + name + " has been added.";
            }
            else if (req.getParameter("action").equals("delete")) {
                if (!this.deleteTrack(name)) {
                    res.sendError(400, "" + name + " is not in the list.");
                    return;
                }
                msg = "" + name + " has been deleted.";
            }
            else if (req.getParameter("action").equals("get")) {
                if (!this.getTracks()) {
                    res.sendError(400, "Cannot get tracks.");
                    return;
                }
                msg = "Got tracks.";
            }
        }
        else if (req.getParameter("action").equals("deposit")) {
            if (!this.addMoney(poid, money)) {
                res.sendError(400, "Cannot add money.");
                return;
            }
            msg = "Added money.";
        }
        res.setContentType("text/html");
        res.setHeader("pragma", "no-cache");
        final PrintWriter out = res.getWriter();
        out.print("<HTML><HEAD><TITLE>Jukebox Manager</TITLE></HEAD><BODY>");
        out.print(msg);
        out.print("<HR><A HREF=\"");
        out.print(req.getRequestURL());
        if (poid != null && poid.length() != 0) {
            out.print("?poid=" + poid);
        }
        out.print("\">Return</A></BODY></HTML>");
        out.close();
    }
    
    public String getServletInfo() {
        return "JukeboxWebEngine";
    }
    
    private synchronized boolean addTrack(final String name, final String type, final String url, final String cost, final String description) throws IOException {
        final JukeboxWebPlugin jukeboxWebPlugin = (JukeboxWebPlugin)Engine.getPlugin("JukeboxWebPlugin");
        if (jukeboxWebPlugin == null) {
            return false;
        }
        if (this.nameList.contains(name)) {
            return false;
        }
        this.nameList.add(name);
        this.typeList.add(type);
        this.urlList.add(url);
        this.costList.add(cost);
        this.descriptionList.add(description);
        jukeboxWebPlugin.addTrack(name, type, url, cost, description);
        return true;
    }
    
    private synchronized boolean deleteTrack(final String name) throws IOException {
        final JukeboxWebPlugin jukeboxWebPlugin = (JukeboxWebPlugin)Engine.getPlugin("JukeboxWebPlugin");
        if (jukeboxWebPlugin == null) {
            return false;
        }
        final int index = this.nameList.indexOf(name);
        if (index == -1) {
            return false;
        }
        this.nameList.remove(index);
        this.typeList.remove(index);
        this.urlList.remove(index);
        this.costList.remove(index);
        this.descriptionList.remove(index);
        jukeboxWebPlugin.deleteTrack(name);
        return true;
    }
    
    private synchronized boolean getTracks() {
        final JukeboxWebPlugin jukeboxWebPlugin = (JukeboxWebPlugin)Engine.getPlugin("JukeboxWebPlugin");
        if (jukeboxWebPlugin == null) {
            return false;
        }
        final ArrayList trackData = jukeboxWebPlugin.getTracks();
        if (trackData == null) {
            return false;
        }
        this.nameList.clear();
        this.typeList.clear();
        this.urlList.clear();
        this.costList.clear();
        this.descriptionList.clear();
        int i = trackData.size();
        while (i-- > 0) {
            final HashMap trackInfo = trackData.get(i);
            this.nameList.add(trackInfo.get("name"));
            this.typeList.add(trackInfo.get("type"));
            this.urlList.add(trackInfo.get("url"));
            this.costList.add(trackInfo.get("cost"));
            this.descriptionList.add(trackInfo.get("description"));
        }
        return true;
    }
    
    private synchronized int getMoney(final String poid) {
        final JukeboxWebPlugin jukeboxWebPlugin = (JukeboxWebPlugin)Engine.getPlugin("JukeboxWebPlugin");
        if (jukeboxWebPlugin == null) {
            return 0;
        }
        final int money = jukeboxWebPlugin.getMoney(poid);
        return money;
    }
    
    private synchronized boolean addMoney(final String poid, final String money) {
        final JukeboxWebPlugin jukeboxWebPlugin = (JukeboxWebPlugin)Engine.getPlugin("JukeboxWebPlugin");
        Double dDollars = new Double(money);
        dDollars *= 100.0;
        final Integer iDollars = new Integer((int)(Object)dDollars);
        if (jukeboxWebPlugin == null) {
            return false;
        }
        jukeboxWebPlugin.addMoney(poid, iDollars.toString());
        return true;
    }
}
