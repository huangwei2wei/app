// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.io.FileOutputStream;
import atavism.server.util.Log;
import java.net.URLEncoder;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;

public class HelperFunctions
{
    public static boolean isAlphaNumeric(final String s) {
        final char[] chars = s.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            final char c = chars[x];
            if (c < 'a' || c > 'z') {
                if (c < 'A' || c > 'Z') {
                    if (c < '0' || c > '9') {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isAlphaNumericWithSpaces(final String s) {
        final char[] chars = s.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            final char c = chars[x];
            if (c < 'a' || c > 'z') {
                if (c < 'A' || c > 'Z') {
                    if (c < '0' || c > '9') {
                        if (c != ' ') {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isAlphaNumericWithSpacesAndApostrophes(final String s) {
        final char[] chars = s.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            final char c = chars[x];
            if (c < 'a' || c > 'z') {
                if (c < 'A' || c > 'Z') {
                    if (c < '0' || c > '9') {
                        if (c != ' ') {
                            if (c != '\'') {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public static String readEncodedString(final byte[] bytes) {
        if (bytes != null) {
            try {
                return new String(bytes, "UTF-8");
            }
            catch (UnsupportedEncodingException ex) {}
        }
        return null;
    }
    
    public static void sendHtmlForm(final String url, final HashMap<String, String> data) {
        try {
            final URL siteUrl = new URL(url);
            final HttpURLConnection hConnection = (HttpURLConnection)siteUrl.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            hConnection.setDoOutput(true);
            hConnection.setDoInput(true);
            hConnection.setRequestMethod("POST");
            final DataOutputStream out = new DataOutputStream(hConnection.getOutputStream());
            final Set<String> keys = data.keySet();
            final Iterator<String> keyIter = keys.iterator();
            String content = "";
            int i = 0;
            while (keyIter.hasNext()) {
                final Object key = keyIter.next();
                if (i != 0) {
                    content = String.valueOf(content) + "&";
                }
                content = String.valueOf(content) + key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
                ++i;
            }
            Log.debug("Sending html form with content: " + content + " to URL: " + url);
            System.out.println(content);
            out.writeBytes(content);
            out.flush();
            out.close();
            hConnection.connect();
            if (200 == hConnection.getResponseCode()) {
                final InputStream is = hConnection.getInputStream();
                final OutputStream os = new FileOutputStream("output.html");
                int dataBytes;
                while ((dataBytes = is.read()) != -1) {
                    os.write(dataBytes);
                }
                is.close();
                os.close();
                hConnection.disconnect();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static boolean CopyTemplateFiles(final String templateName, final String islandName) {
        try {
            final String source = "../island_templates/" + templateName;
            final String target = "../config/iow/" + islandName;
            new File(target).mkdir();
            final File dir = new File(source);
            final String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) {
                final String filename = children[i];
                final String newFilename = filename.replace(templateName, islandName);
                Log.debug("Copying file: " + filename + " to: " + newFilename);
                final File fromFile = new File(String.valueOf(source) + "/" + filename);
                final File toFile = new File(String.valueOf(target) + "/" + newFilename);
                final FileReader in = new FileReader(fromFile);
                final FileWriter out = new FileWriter(toFile);
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                in.close();
                out.close();
                if ((filename.contains(".aow") || filename.contains(".mmf")) && !ModifyWorldFile(templateName, islandName, String.valueOf(target) + "/" + newFilename)) {
                    return false;
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    private static boolean ModifyWorldFile(final String templateName, final String islandName, final String fileName) {
        try {
            final File file = new File(fileName);
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            String oldtext = "";
            while ((line = reader.readLine()) != null) {
                oldtext = String.valueOf(oldtext) + line + "\r\n";
            }
            reader.close();
            final String newtext = oldtext.replaceAll(templateName, islandName);
            final FileWriter writer = new FileWriter(fileName);
            writer.write(newtext);
            writer.close();
        }
        catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException e2) {
            return false;
        }
        return true;
    }
}
