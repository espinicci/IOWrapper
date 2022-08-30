/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.fi.sestofiorentino.comune.utilities;

import jakarta.json.JsonObject;
import jakarta.ws.rs.HttpMethod;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;


import org.apache.tomcat.jakartaee.commons.io.IOUtils;

/**
 * Classe di utilità per eseguire chiamate HTTP
 * @author emilios
 */
public class HttpClient {

//    /**
//     * Factory method della classe HttpClient
//     * @return HttpClient instance (singleton)
//     */
//    public static synchronized CloseableHttpClient getInstance() {
//
//        if (singletonHttpClient == null) {
//            singletonHttpClient = HttpClients.createDefault();
//        }
//
//        return singletonHttpClient;
//
//    }
//
//    private static CloseableHttpClient singletonHttpClient = null;

    /**
     * Esegue una request HTTP
     * @param url URL http invocata dalla chiamata
     * @param method metodo HTTP (POST, GET, PUT)
     * @param headers
     * @param params lista dei parametri della chiamata
     * @return
     * @throws IOException
     */
    public static String doRequest(String url, String method, List<Header> headers, List<NameValuePair> params) throws IOException {
        return doRequest(url, method, headers, params, null);
    }
    
    /**
     * Esegue una request HTTP
     * @param url URL http invocata dalla chiamata
     * @param method metodo HTTP (POST, GET, PUT)
     * @param headers
     * @param payload
     * @return
     * @throws IOException
     */
    public static String doRequest(String url, String method, List<Header> headers, JsonObject payload) throws IOException {
        return doRequest(url, method, headers, null, payload);
    }
    
    
    
    /**
     * Esegue una request HTTP
     * @param url URL http invocata dalla chiamata
     * @param method metodo HTTP (POST, GET, PUT)
     * @param headers
     * @param params lista dei parametri della chiamata
     * @param payload
     * @return
     * @throws IOException
     */
    public static String doRequest(String url, String method, List<Header> headers, List<NameValuePair> params, JsonObject payload) throws IOException {

        String responseContent = null;

        String httpClientConnectionTimeOut = ConfigurationManager.getInstance().getConfigProperties().getProperty("httpClient.connection.timeout");
        
        String httpClientResponseTimeOut = ConfigurationManager.getInstance().getConfigProperties().getProperty("httpClient.response.timeout");
        
        String socketTimeOut = ConfigurationManager.getInstance().getConfigProperties().getProperty("httpClient.socket.timeout");
        
//        CloseableHttpClient httpClient = HttpClient.getInstance();

PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
      .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
              .setSslContext(SSLContexts.createSystemDefault())
              .setTlsVersions(TLS.V_1_3, TLS.V_1_2)
              .build())
      .setDefaultSocketConfig(SocketConfig.custom()
              .setSoTimeout(Timeout.ofSeconds(Long.parseLong(socketTimeOut)))
              .build())
      .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
      .setConnPoolPolicy(PoolReusePolicy.LIFO)
      .setConnectionTimeToLive(TimeValue.ofMinutes(1L))
      .build();

CloseableHttpClient httpClient = HttpClients.custom()
                                            .setConnectionManager(connectionManager)
                                            .setDefaultRequestConfig(RequestConfig.custom()
                                                .setConnectTimeout(Timeout.ofSeconds(Long.parseLong(httpClientConnectionTimeOut)))
                                                .setResponseTimeout(Timeout.ofSeconds(Long.parseLong(httpClientResponseTimeOut)))
                                                .setCookieSpec(StandardCookieSpec.STRICT)
                                                .build())
                                            .setDefaultHeaders(headers)
                                            .build();

CookieStore cookieStore = new BasicCookieStore();

CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

String requestConfigConnectionTimeOut = ConfigurationManager.getInstance().getConfigProperties().getProperty("requestConfig.connection.timeout");
        
String requestConfigResponseTimeOut = ConfigurationManager.getInstance().getConfigProperties().getProperty("requestConfig.response.timeout");

HttpClientContext clientContext = HttpClientContext.create();
clientContext.setCookieStore(cookieStore);
clientContext.setCredentialsProvider(credentialsProvider);
clientContext.setRequestConfig(RequestConfig.custom()
      .setConnectTimeout(Timeout.ofSeconds(Long.parseLong(requestConfigConnectionTimeOut)))
      .setResponseTimeout(Timeout.ofSeconds(Long.parseLong(requestConfigResponseTimeOut)))
      .build());
        
        CloseableHttpResponse response = null;
        StringEntity entity = null;

        try {

            if (null != params) {
                entity = new UrlEncodedFormEntity(params, UTF_8);
            } else if (payload != null) {
                entity = new StringEntity(payload.toString(),ContentType.APPLICATION_JSON);
            }
            if (method.toUpperCase().equals(HttpMethod.POST)) {
                HttpPost httpPost = new HttpPost(url);
                if (null != entity) {
                    httpPost.setEntity(entity);
                }
                response = httpClient.execute(httpPost);
            } else if (method.toUpperCase().equals(HttpMethod.PUT)) {
                HttpPut httpPut = new HttpPut(url);
                if (null != entity) {
                    httpPut.setEntity(entity);
                }
                response = httpClient.execute(httpPut);
            } else if (method.toUpperCase().equals(HttpMethod.GET)) {
                HttpGet httpGet = new HttpGet(url);
                response = httpClient.execute(httpGet);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {

                responseContent = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

                System.out.println(responseContent);

                response.close();

            } else {
                System.out.println("ERROR: NULL response!");
            }

            httpClient.close(CloseMode.GRACEFUL);
            
        }

        return responseContent;

    }

}
