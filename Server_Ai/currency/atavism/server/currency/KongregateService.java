// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;
import java.util.Map;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.util.Log;
import org.json.simple.JSONObject;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import org.json.simple.parser.JSONParser;

public final class KongregateService
{
    private static final String KongregateURL = "http://www.kongregate.com";
    private final String apiKey;
    private final JSONParser parser;
    
    public KongregateService(final String apiKey) {
        this.parser = new JSONParser();
        this.apiKey = apiKey;
    }
    
    public Boolean authenticate(final String userId, final String gameAuthToken) {
        final String getURL = "http://www.kongregate.com/api/authenticate.json?api_key=" + this.apiKey + "&user_id=" + userId + "&game_auth_token=" + gameAuthToken;
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
            final JSONObject jsonMap = (JSONObject)this.parser.parse(resultString);
            Log.debug("kongregate_service.authenticate(): jsonMap=" + jsonMap);
            if (jsonMap != null && jsonMap.containsKey((Object)"success")) {
                return (Boolean)jsonMap.get((Object)"success");
            }
        }
        catch (Exception e) {
            Log.error("kongregate_service.authenticate(): " + e);
            return false;
        }
        return null;
    }
    
    public HashMap<String, Serializable> getItemsList() {
        final String getURL = "http://www.kongregate.com/api/items.json?api_key=" + this.apiKey;
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
            final JSONObject jsonMap = (JSONObject)this.parser.parse(resultString);
            return new HashMap<String, Serializable>((Map<? extends String, ? extends Serializable>)jsonMap);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public HashMap<String, Serializable> getUserItems(final String userId) {
        final String getURL = "http://www.kongregate.com/api/user_items.json?api_key=" + this.apiKey + "&user_id=" + userId;
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
            final JSONObject jsonMap = (JSONObject)this.parser.parse(resultString);
            return new HashMap<String, Serializable>((Map<? extends String, ? extends Serializable>)jsonMap);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public Boolean useItem(final String userId, final String gameAuthToken, final String itemId) {
        final String postURL = "http://www.kongregate.com/api/use_item.json";
        final NameValuePair[] postData = { new NameValuePair("api_key", this.apiKey), new NameValuePair("game_auth_token", gameAuthToken), new NameValuePair("user_id", userId), new NameValuePair("id", itemId) };
        final HttpClient client = new HttpClient();
        final PostMethod postMethod = new PostMethod(postURL);
        postMethod.setRequestBody(postData);
        postMethod.setFollowRedirects(false);
        int statusCode = 0;
        String resultString = null;
        try {
            statusCode = client.executeMethod((HttpMethod)postMethod);
            resultString = postMethod.getResponseBodyAsString();
            Log.debug("kongregate_service.useItem(): statusCode=" + statusCode + ", resultString=" + resultString);
        }
        catch (Exception e) {
            Log.error("kongregate_service.useItem() Exception: " + e);
            return false;
        }
        Log.debug("kongregate_service.useItem(): resultString=" + resultString);
        try {
            final JSONObject jsonMap = (JSONObject)this.parser.parse(resultString);
            Log.debug("kongregate_service.useItem(): jsonMap=" + jsonMap);
            if (jsonMap != null && jsonMap.containsKey((Object)"success")) {
                return (Boolean)jsonMap.get((Object)"success");
            }
        }
        catch (Exception e) {
            Log.error("kongregate_service.useItem(): " + e);
            return false;
        }
        return false;
    }
}
