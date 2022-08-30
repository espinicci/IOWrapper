/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package it.fi.sestofiorentino.comune.webservices;

import it.fi.sestofiorentino.comune.utilities.ConfigurationManager;
import it.fi.sestofiorentino.comune.utilities.HttpClient;
import it.fi.sestofiorentino.comune.utilities.JSONUtils;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;


/**
 * REST Web Service
 *
 * @author emilios
 */
@Path("ioWrapperResource")
public class IOWrapperResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of IOWrapperResource
     */
    public IOWrapperResource() {
    }

    /**
     * Retrieves representation of an instance of it.fi.sestofiorentino.comune.webservices.IOWrapperResource
     * @param serviceID
     * @param cf
     * @return an instance of java.lang.String
     */
    @GET
    @Path("profile/{serviceID}/{cf}")
    @Produces(MediaType.TEXT_HTML)
    public String getProfile(@PathParam("serviceID") String serviceID,
                             @PathParam("cf") String cf) {
        return this.getProfileFromCF(cf, serviceID);
    }

    
    @GET
    @Path("testMessage/{serviceID}/{cf}")
    @Produces(MediaType.TEXT_HTML)
    public String postMessage(@PathParam("serviceID") String serviceID,
                              @PathParam("cf") String cf) {
       
        return this.getIOTestMessage(cf, serviceID);
    }
    
    @POST
    @Path("testMessage")
    @Produces(MediaType.TEXT_HTML)
    public String postMessage(@FormParam("cf") String cf,
                              @FormParam("iuv") String iuv,
                              @FormParam("serviceID") String serviceID) {
       
        return this.postIOMessage(cf, null, null, iuv, serviceID);
    }
    
    @POST
    @Path("message")
    @Produces(MediaType.TEXT_HTML)
    public String postMessage(@FormParam("cf") String cf,
                              @FormParam("markdown") String markdown,
                              @FormParam("amount") String amount,
                              @FormParam("iuv") String iuv,
                              @FormParam("serviceID") String serviceID) {
       
        return this.postIOMessage(cf, markdown, amount, iuv, serviceID);
    }
    
    
    /* Private Methods */
    
    private String getIOTestMessage(String cf, String serviceID) {
        
        String jsonStringSenderAllowed = this.getProfileFromCF(cf, serviceID);
        String response = null;
        StringReader reader = new StringReader(jsonStringSenderAllowed);
        boolean senderAllowed = false;
        JsonParser parser = Json.createParser(reader);
        while (parser.hasNext() && !senderAllowed) {
            Event event = parser.next();
            if (event == JsonParser.Event.KEY_NAME ) {
               String key = parser.getString();
               if (key.equals("sender_allowed")) {
                   event = parser.next();
                   senderAllowed = ((event==Event.VALUE_TRUE)?true:false);
               }
            }
        }
        
        if (senderAllowed) {
            response = this.postTestMessage(cf, serviceID);    
        } else {
            response = jsonStringSenderAllowed;
        }

        return response;

   } 
    
    
    private String getProfileFromCF(String cf, String serviceID) {
        
        ConfigurationManager config = ConfigurationManager.getInstance();
        
        String subscriptionKeyHeader = config.getConfigProperties().getProperty("appIO.subscriptionKey.header");
        String subscriptionKey = config.getServiceProperties().getProperty(serviceID);
        
//        Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        Header header = new BasicHeader(subscriptionKeyHeader, subscriptionKey);
        List<Header> headers = new ArrayList();
        headers.add(header);
        
        String urlOverHttps = config.getConfigProperties().getProperty("appIO.profile.URL");
        String response = "";
        try {
            response = HttpClient.doRequest(urlOverHttps+cf, HttpMethod.GET, headers, null, null);
        } catch (IOException ex) {
            Logger.getLogger(IOWrapperResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;

    }

    private String postIOMessage(String cf, String markdown, String amount, String iuv, String serviceID) {
        
        ConfigurationManager config = ConfigurationManager.getInstance();
        
        String subscriptionKeyHeader = config.getConfigProperties().getProperty("appIO.subscriptionKey.header");
        String subscriptionKey = config.getServiceProperties().getProperty(serviceID);
        
//        Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        Header header = new BasicHeader(subscriptionKeyHeader, subscriptionKey);
        List<Header> headers = new ArrayList();
        headers.add(header);
        
        String urlOverHttps = config.getConfigProperties().getProperty("appIO.message.URL");
        String response = "";
        try {
            response = HttpClient.doRequest(urlOverHttps+cf, HttpMethod.POST, headers, null, null);
        } catch (IOException ex) {
            Logger.getLogger(IOWrapperResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
        
    }
    
    private String postTestMessage(String cf, String serviceID) {
        
        ConfigurationManager config = ConfigurationManager.getInstance();
        
        String subscriptionKeyHeader = config.getConfigProperties().getProperty("appIO.subscriptionKey.header");
        String subscriptionKey = config.getServiceProperties().getProperty(serviceID);
        
//        Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        Header header = new BasicHeader(subscriptionKeyHeader, subscriptionKey);
        List<Header> headers = new ArrayList();
        headers.add(header);
        
        JsonObject payload = JSONUtils.getInstance().getTestMessagePayload(cf);
        
        
        String urlOverHttps = config.getConfigProperties().getProperty("appIO.message.URL");
        String response = "";
        try {
            response = HttpClient.doRequest(urlOverHttps, HttpMethod.POST, headers, payload);
        } catch (IOException ex) {
            Logger.getLogger(IOWrapperResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
        
    }
    
    
}
