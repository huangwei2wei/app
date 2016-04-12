package com.wyd.channel.utils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
/**
 * http通信辅助类
 * 
 * @author guoqiu_zeng
 */
public class HttpClientUtil {
    /**
     * 通过body方式传递数据
     * 
     * @param url
     * @param data
     * @return
     * @throws IOException
     */
    public static String PostData(String url, String data) throws IOException {
        HttpClient httpClient = new HttpClient();
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        PostMethod postMethod = new PostMethod(url);
        InputStream retData = null;
        String ret = "";
        try {
            postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
            if (null != data && data.length() > 0) {
                byte[] dataByte = data.getBytes();
                InputStream inputStream = new ByteArrayInputStream(dataByte);
                RequestEntity re = new InputStreamRequestEntity(inputStream, dataByte.length);
                postMethod.setRequestEntity(re);
            }
            httpClient.executeMethod(postMethod);
            retData = postMethod.getResponseBodyAsStream();
            StringBuffer sb = new StringBuffer();
            if (null != retData) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(retData, "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            ret = sb.toString();
        } catch (Exception ex) {
        } finally {
            if (null != retData) retData.close();
            postMethod.abort();
            postMethod.releaseConnection();
        }
        return ret;
    }

    /**
     * 普通的getdata数据方式
     * 
     * @param url
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public static String GetData(String url) throws HttpException, IOException {
        HttpClient client = new HttpClient();
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        GetMethod method = new GetMethod(url);
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
        String ret = null;
        try {
            client.executeMethod(method);
            ret = method.getResponseBodyAsString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            method.abort();
            method.releaseConnection();
        }
        return ret;
    }
    
    /**
     * author by lpp created at 2010-7-26 上午09:29:33
     */
    public static class MySSLProtocolSocketFactory implements ProtocolSocketFactory {
        private SSLContext sslcontext = null;

        private SSLContext createSSLContext() {
            SSLContext sslcontext = null;
            try {
                sslcontext = SSLContext.getInstance("SSL");
                sslcontext.init(null, new TrustManager[] { new TrustAnyTrustManager()}, new java.security.SecureRandom());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            return sslcontext;
        }

        private SSLContext getSSLContext() {
            if (this.sslcontext == null) {
                this.sslcontext = createSSLContext();
            }
            return this.sslcontext;
        }

        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(host, port);
        }

        public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
        }

        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
            if (params == null) {
                throw new IllegalArgumentException("Parameters may not be null");
            }
            int timeout = params.getConnectionTimeout();
            SocketFactory socketfactory = getSSLContext().getSocketFactory();
            if (timeout == 0) {
                return socketfactory.createSocket(host, port, localAddress, localPort);
            } else {
                Socket socket = socketfactory.createSocket();
                SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
                SocketAddress remoteaddr = new InetSocketAddress(host, port);
                socket.bind(localaddr);
                socket.connect(remoteaddr, timeout);
                return socket;
            }
        }
        // 自定义私有类
        private class TrustAnyTrustManager implements X509TrustManager {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        }
    }
}
