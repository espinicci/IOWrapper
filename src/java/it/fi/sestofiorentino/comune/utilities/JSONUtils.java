/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Singleton.java to edit this template
 */
package it.fi.sestofiorentino.comune.utilities;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

/**
 *
 * @author emilios
 */
public class JSONUtils {
    
    private JSONUtils() {
    }
    
    public static JSONUtils getInstance() {
        return JSONUtilsHolder.INSTANCE;
    }
    
    private static class JSONUtilsHolder {

        private static final JSONUtils INSTANCE = new JSONUtils();
    }
    
    public JsonObject getTestMessagePayload(String cf) {
        return getMessagePayload(cf, null, null, null, null);
    }
   
    public JsonObject getMessagePayload(String cf, String subject, String message, String iuv, Integer amount) {
        
        JsonObjectBuilder outObjBuilder = Json.createObjectBuilder();
        JsonObject json = null;
        
        int time_to_live=3600;

        String _subject = null;
        String _message = null;
        String _iuv = null;
        Integer _amount = null;
        
        ConfigurationManager config = ConfigurationManager.getInstance();
        
        if (null == message) {
            
            //messaggio di test
            
            _subject = config.getConfigProperties().getProperty("appIO.test.subject");
            _message = config.getConfigProperties().getProperty("appIO.test.message");
            _iuv = config.getConfigProperties().getProperty("appIO.test.IUV");
            _amount = Integer.valueOf(1);
            
        } else { 
            
            _subject = subject;
            _message = message;
            _amount = amount;
            _iuv = iuv;
            
        }
        
        try {

            outObjBuilder.add("time_to_live", time_to_live);
            JsonObjectBuilder contentBuilder = Json.createObjectBuilder();
            contentBuilder.add("subject", _subject);
            contentBuilder.add("markdown", _message);
            JsonObjectBuilder paymentBuilder = Json.createObjectBuilder();
            paymentBuilder.add("amount", _amount);
            paymentBuilder.add("notice_number", _iuv);
            paymentBuilder.add("invalid_after_due_date", Boolean.FALSE);
            contentBuilder.add("payment_data", paymentBuilder);
            outObjBuilder.add("content", contentBuilder);
            outObjBuilder.add("fiscal_code", cf);
            outObjBuilder.add("feature_level_type", "STANDARD");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            outObjBuilder = Json.createObjectBuilder().add("errore", ex.getMessage());

        } finally {
            json = outObjBuilder.build();
        }

        return json;
        
    }
    
}
