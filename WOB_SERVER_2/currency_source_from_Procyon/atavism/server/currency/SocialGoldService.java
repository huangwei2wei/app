// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.currency;

import java.util.StringTokenizer;
import atavism.server.util.Log;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import java.math.BigInteger;
import java.security.MessageDigest;

public final class SocialGoldService
{
    private static final String SocialGoldVersion = "v1/";
    private static final String SocialGoldURL_LIVE = "http://api.jambool.com/socialgold/";
    private static final String SocialGoldURL_DEBUG = "http://api.sandbox.jambool.com/socialgold/";
    private final String SocialGoldURL;
    private final String SocialGoldOfferID;
    private final String SecretKey;
    
    public SocialGoldService(final String offerID, final String secretKey) {
        this.SocialGoldURL = "http://api.jambool.com/socialgold/";
        this.SocialGoldOfferID = offerID;
        this.SecretKey = secretKey;
    }
    
    public SocialGoldService(final String offerID, final String secretKey, final boolean live) {
        if (live) {
            this.SocialGoldURL = "http://api.jambool.com/socialgold/";
        }
        else {
            this.SocialGoldURL = "http://api.sandbox.jambool.com/socialgold/";
        }
        this.SocialGoldOfferID = offerID;
        this.SecretKey = secretKey;
    }
    
    private String computeSignature(final String BaseString) {
        String Signature = "";
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(BaseString.getBytes("iso-8859-1"), 0, BaseString.length());
            final byte[] md5hash = md.digest();
            final BigInteger SI = new BigInteger(1, md5hash);
            Signature = String.format("%032x", SI);
        }
        catch (Exception ex) {}
        return Signature;
    }
    
    private Long doRequest(final String BaseURL, final String BaseString, final String ParamString) {
        final String Signature = this.computeSignature(BaseString);
        final String AuthenticatedURL = BaseURL + "&sig=" + Signature + "&format=json" + ParamString;
        HttpURL HelperURL = null;
        try {
            HelperURL = new HttpURL(AuthenticatedURL);
        }
        catch (Exception e2) {
            return null;
        }
        final String EscapedAuthenticatedURL = HelperURL.toString();
        final HttpClient client = new HttpClient();
        final GetMethod method = new GetMethod(EscapedAuthenticatedURL);
        method.setFollowRedirects(true);
        int statusCode = 0;
        String Result = null;
        try {
            statusCode = client.executeMethod((HttpMethod)method);
            Result = method.getResponseBodyAsString();
            Log.debug("doRequest(): Result=" + Result);
        }
        catch (Exception e) {
            System.out.println("Exception received: " + e);
            Log.exception("SocialGoldService.doRequest - Exception occured during http request ", e);
        }
        Long userBalance = null;
        if (statusCode == 200) {
            StringTokenizer st;
            String token;
            for (st = new StringTokenizer(Result), token = null, token = st.nextToken(); !token.equals("\"user_balance\"") && st.hasMoreTokens(); token = st.nextToken()) {}
            st.nextToken();
            userBalance = Long.parseLong(st.nextToken().replace("\"", " ").trim());
        }
        else {
            Log.warn("SocialGoldService.doRequest - get balance failed with status code = " + statusCode);
        }
        return userBalance;
    }
    
    public boolean createUser(final String UserID, final Long amount) {
        final String SocialGoldAction = "credit";
        final String CreditType = "new_user_credit";
        final String Description = "create new user";
        final String Timestamp = "" + System.currentTimeMillis() / 1000L;
        final String ExternalReferenceID = "User-Create-RefID-" + UserID + "-" + Timestamp;
        final String BaseString = "action" + SocialGoldAction + "amount" + amount + "credit_type" + CreditType + "description" + Description + "external_ref_id" + ExternalReferenceID + "offer_id" + this.SocialGoldOfferID + "ts" + Timestamp + "user_id" + UserID + this.SecretKey;
        final String BaseURL = this.SocialGoldURL + "v1/" + this.SocialGoldOfferID + "/" + UserID + "/" + SocialGoldAction + "/?ts=" + Timestamp;
        final String ParamString = "&amount=" + amount + "&credit_type=" + CreditType + "&description=" + Description + "&external_ref_id=" + ExternalReferenceID;
        return this.doRequest(BaseURL, BaseString, ParamString) != null;
    }
    
    public Long getBalance(final String UserID) {
        final String SocialGoldAction = "get_balance";
        final String Timestamp = "" + System.currentTimeMillis() / 1000L;
        final String BaseString = "action" + SocialGoldAction + "error_on_unknown_user" + "1" + "offer_id" + this.SocialGoldOfferID + "ts" + Timestamp + "user_id" + UserID + this.SecretKey;
        final String BaseURL = this.SocialGoldURL + "v1/" + this.SocialGoldOfferID + "/" + UserID + "/" + SocialGoldAction + "/?ts=" + Timestamp;
        final String ParamString = "&error_on_unknown_user=1";
        return this.doRequest(BaseURL, BaseString, ParamString);
    }
    
    public Long credit(final String UserID, final Long amount, final String Description) {
        final String SocialGoldAction = "credit";
        final String CreditType = "item_sold_credit";
        final String Timestamp = "" + System.currentTimeMillis() / 1000L;
        final String ExternalReferenceID = "Item-Sale-RefID-" + UserID + "-" + Timestamp;
        final String BaseString = "action" + SocialGoldAction + "amount" + amount + "credit_type" + CreditType + "description" + Description + "external_ref_id" + ExternalReferenceID + "offer_id" + this.SocialGoldOfferID + "ts" + Timestamp + "user_id" + UserID + this.SecretKey;
        final String BaseURL = this.SocialGoldURL + "v1/" + this.SocialGoldOfferID + "/" + UserID + "/" + SocialGoldAction + "/?ts=" + Timestamp;
        final String ParamString = "&amount=" + amount + "&credit_type=" + CreditType + "&description=" + Description + "&external_ref_id=" + ExternalReferenceID;
        return this.doRequest(BaseURL, BaseString, ParamString);
    }
    
    public Long debit(final String UserID, final Long amount, final String Description) {
        final String SocialGoldAction = "debit";
        final String Timestamp = "" + System.currentTimeMillis() / 1000L;
        final String ExternalReferenceID = "Item-Buy-RefID-" + UserID + "-" + Timestamp;
        final String BaseString = "action" + SocialGoldAction + "amount" + amount + "description" + Description + "external_ref_id" + ExternalReferenceID + "offer_id" + this.SocialGoldOfferID + "ts" + Timestamp + "user_id" + UserID + this.SecretKey;
        final String BaseURL = this.SocialGoldURL + "v1/" + this.SocialGoldOfferID + "/" + UserID + "/" + SocialGoldAction + "/?ts=" + Timestamp;
        final String ParamString = "&amount=" + amount + "&description=" + Description + "&external_ref_id=" + ExternalReferenceID;
        return this.doRequest(BaseURL, BaseString, ParamString);
    }
}
