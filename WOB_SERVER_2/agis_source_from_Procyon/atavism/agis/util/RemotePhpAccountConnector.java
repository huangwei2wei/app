// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import java.net.URLConnection;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.net.URL;
import atavism.server.util.Log;
import atavism.server.objects.RemoteAccountConnector;

public class RemotePhpAccountConnector extends RemoteAccountConnector
{
    private String url;
    
    public RemotePhpAccountConnector() {
        this.url = "http://yourdomain.com/verifyAccount.php";
    }
    
    public boolean verifyAccount(final String accountName, final String password) {
        Log.debug("CONNECTOR: verifying account with wordpress connection");
        try {
            String res = "";
            final URL urlObj = new URL(this.url);
            final URLConnection lu = urlObj.openConnection();
            final String data = "user=" + URLEncoder.encode(accountName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
            lu.setDoOutput(true);
            final OutputStreamWriter wr = new OutputStreamWriter(lu.getOutputStream());
            wr.write(data);
            wr.flush();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                res = String.valueOf(res) + line;
            }
            Log.debug("PHP: response: " + res);
            wr.flush();
            wr.close();
            System.out.println(res);
            if (res.equals("Success")) {
                return true;
            }
        }
        catch (Exception ex) {}
        return false;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
