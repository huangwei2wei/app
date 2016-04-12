package com.app.empire.world;

import java.util.HashMap;
import java.util.Map;

import com.app.empire.world.common.util.HttpClientUtil;
import com.app.empire.world.purchase.Order;

import net.sf.json.JSONObject;

public class Test {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// String name = "荆棘王冠";
		//
		// try {
		// System.out.println(KeywordsUtil.isInvalidName(name.toLowerCase()));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// String name = "6C1E7E6F01001051EA0A9E50BB7CAA36BF7D0000";
		// try {
		//
		// System.out.println(CryptionUtil.Decrypt(CryptionUtil.Encrypt(name,
		// "pifnwkjdhn"), "pifnwkjdhn"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		String url = "https://buy.itunes.apple.com/verifyReceipt";
		String data = "ewoJInNpZ25hdHVyZSIgPSAiQWtQbkZ3c1M4ejNCemFVS3h4anJTK0RCb041MFFSb0tGNVFFenRkem1rZ1BoQkhERHZQVmF0NzVNNDZsdjZCaXdyNGp1UlZac2NicXFSaDZLWFZLbjA3WmFhZi9BSUpBWGxjSlFqSEw3a1luM0tBT2IxK2hqazJCSy92YXpzeG5Id0xZL04wdG9McXl6M0dTTkpmQVVYNXdQQjFOelg2T0RmMHRxdW5QSzdiMUFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnamltTHdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg5RDU1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STStRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQTFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVTUxcnhmY3FBQWU1QzIvZkVXOEtVbDRpTzRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllISytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQcFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WTRFY1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU0dZejErVTNzRHhKemViU3BiYUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUwTFRBNUxUSTJJREU1T2pVNU9qTXlJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakUwTVRFM09EWTNOekkxTlRNaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJMlltWTJOR1U0TVROa1ptUTRaVFV4WVRKaVlqaGxNakZsWlRVeU1UQTROV05rTjJaaVlUTmlJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU16WXdNREF3TURnM01UQTRNVFl3SWpzS0NTSmlkbkp6SWlBOUlDSXhMamt1TVNJN0Nna2lZWEJ3TFdsMFpXMHRhV1FpSUQwZ0lqVTVORFF4TWpJeE15STdDZ2tpZEhKaGJuTmhZM1JwYjI0dGFXUWlJRDBnSWpNMk1EQXdNREE0TnpFd09ERTJNQ0k3Q2draWNYVmhiblJwZEhraUlEMGdJakVpT3dvSkltOXlhV2RwYm1Gc0xYQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakUwTVRFM09EWTNOekkxTlRNaU93b0pJblZ1YVhGMVpTMTJaVzVrYjNJdGFXUmxiblJwWm1sbGNpSWdQU0FpUWtVME9UWkJPVEl0TjBFNU9TMDBNa00zTFVKRlFVTXRNa1l5TkRnd01EQXdORUUySWpzS0NTSnBkR1Z0TFdsa0lpQTlJQ0kxT1RRME5qZ3pORFlpT3dvSkluWmxjbk5wYjI0dFpYaDBaWEp1WVd3dGFXUmxiblJwWm1sbGNpSWdQU0FpTmpjek5qQXlOalEwSWpzS0NTSndjbTlrZFdOMExXbGtJaUE5SUNKamIyMHVhRzluWVcxbExtSnZiV0p2Ym14cGJtVXVNU0k3Q2draWNIVnlZMmhoYzJVdFpHRjBaU0lnUFNBaU1qQXhOQzB3T1MweU55QXdNam8xT1Rvek1pQkZkR012UjAxVUlqc0tDU0p2Y21sbmFXNWhiQzF3ZFhKamFHRnpaUzFrWVhSbElpQTlJQ0l5TURFMExUQTVMVEkzSURBeU9qVTVPak15SUVWMFl5OUhUVlFpT3dvSkltSnBaQ0lnUFNBaVkyOXRMbWh2Wnk1a1lXNWtZVzVrWVc4dWIyd2lPd29KSW5CMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUwTFRBNUxUSTJJREU1T2pVNU9qTXlJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dwOSI7CgkicG9kIiA9ICIzNiI7Cgkic2lnbmluZy1zdGF0dXMiID0gIjAiOwp9";
		try {
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("receipt-data", data);
			JSONObject jsonObject = JSONObject.fromObject(dataMap);
			HttpClientUtil httpClient = new HttpClientUtil();
			String receipt = httpClient.PostData(url, jsonObject.toString());
			jsonObject = JSONObject.fromObject(receipt);
			@SuppressWarnings("static-access")
			Order order = (Order) jsonObject.toBean(jsonObject, Order.class);
			System.out.println(order.getStatus() + "--");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// try {
		// // String deviceToken =
		// "b02ea7d6ae0b3da366bd6d3687f771d0adae537a32ccf332f6f16300b95de583";//高山
		// String deviceToken =
		// "35b4eb356dd5d7406fa827a9b3522f91611630b2d94872f28b45609f97ddc898";//
		// 国求
		// PayLoad payLoad = new PayLoad();
		// payLoad.addAlert("我的push测试");
		// payLoad.addBadge(1);
		// payLoad.addSound("default");
		// PushNotificationManager pushManager =
		// PushNotificationManager.getInstance();
		// // Connect to APNs
		// String host = "gateway.push.apple.com";
		// int port = 2195;
		// String certificatePath = System.getProperty("user.dir") +
		// "/aps_developer_identity.p12.cn";
		// String certificatePassword = "wydwyd";
		// pushManager.initializeConnection(host, port, certificatePath,
		// certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
		// // Send Push
		// pushManager.addDevice("iPhone", deviceToken);
		// Device client = pushManager.getDevice("iPhone");
		// pushManager.sendNotification(client, payLoad);
		// pushManager.removeDevice("iPhone");
		// pushManager.stopConnection();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// //android推送测试
		// HttpClientUtil httpClient = new HttpClientUtil();
		// String pushurl= "http://192.168.1.116:8080/apn/notification.do";
		// try {
		// List<NameValuePair> data = new ArrayList<NameValuePair>();
		// data.add(new NameValuePair("action","push"));
		// data.add(new NameValuePair("broadcast","N"));
		// data.add(new
		// NameValuePair("username","0000000000000000000000000000000000000000"));
		// data.add(new NameValuePair("title","test"));
		// data.add(new NameValuePair("message","adsadfad"));
		// data.add(new NameValuePair("uri",""));
		// System.out.println(httpClient.PostData(pushurl, data));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// for (int i = 0; i < 100; i++) {
		// MyThread myThread = new MyThread();
		// myThread.start();
		// }

		System.out.println(Math.round(6.6f / 0.3f));
	}
}
