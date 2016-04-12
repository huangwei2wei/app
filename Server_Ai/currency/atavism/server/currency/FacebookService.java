// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import atavism.server.util.Log;
import org.json.simple.JSONObject;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import java.math.BigInteger;
import java.security.MessageDigest;
import org.json.simple.parser.JSONParser;

public final class FacebookService
{
    private static final String FacebookRESTserverURL = "http://api.facebook.com/restserver.php";
    private final String apiKey;
    private final String secretKey;
    private final JSONParser parser;
    
    public FacebookService(final String apiKey, final String secretKey) {
        this.parser = new JSONParser();
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }
    
    public String createSignature(final String fbParams) {
        String computedSignature = null;
        try {
            final String input = fbParams + this.secretKey;
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes("iso-8859-1"), 0, input.length());
            computedSignature = String.format("%032x", new BigInteger(1, md.digest()));
        }
        catch (Exception ex) {}
        return computedSignature;
    }
    
    public boolean verifySignature(final String fbParams, final String signature) {
        return signature.equals(this.createSignature(fbParams));
    }
    
    public boolean authenticate(final String userId, final String sessionKey) {
        final String apiParam = "api_key=" + this.apiKey;
        final String callIdParam = "call_id=" + System.currentTimeMillis();
        final String formatParam = "format=JSON";
        final String methodParam = "method=users.getLoggedInUser";
        final String sessionParam = "session_key=" + sessionKey;
        final String versionParam = "v=1.0";
        final String fbParams = apiParam + callIdParam + formatParam + methodParam + sessionParam + versionParam;
        final String signature = this.createSignature(fbParams);
        final String getURL = "http://api.facebook.com/restserver.php?" + methodParam + "&" + apiParam + "&" + callIdParam + "&" + formatParam + "&" + sessionParam + "&" + versionParam + "&sig=" + signature;
        final HttpClient client = new HttpClient();
        final GetMethod getMethod = new GetMethod(getURL);
        getMethod.setFollowRedirects(true);
        int statusCode = 0;
        String resultString = null;
        try {
            statusCode = client.executeMethod((HttpMethod)getMethod);
            resultString = getMethod.getResponseBodyAsString();
        }
        catch (Exception ex) {}
        try {
            final Object obj = this.parser.parse(resultString);
            if (obj instanceof String) {
                return userId.equals(obj);
            }
            if (obj instanceof JSONObject) {
                return false;
            }
        }
        catch (Exception e) {
            Log.exception("FacebookService.validateSession()", e);
        }
        return false;
    }
}
