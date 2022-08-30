/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Singleton.java to edit this template
 */
package it.fi.sestofiorentino.comune.utilities;

import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author emilios
 */
public class ConfigurationManager {
    
    private ConfigurationManager() {

        ResourceBundle rb = ResourceBundle.getBundle("/it/fi/sestofiorentino/comune/config");

        Iterator<String> keys = rb.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            
            if (null == configProperties) configProperties = new Properties();
            if (null == serviceProperties) serviceProperties = new Properties();

            if (key.startsWith("appIO.service")) {
                String stringArr[] = rb.getString(key).split("\\|");
                String serviceID = stringArr[0];
//                String serviceName = stringArr[1];
                String serviceKey = stringArr[2];
                serviceProperties.setProperty(serviceID, serviceKey);
            } else {
                configProperties.setProperty(key, rb.getString(key));
            }   
        }
    }
    
    
    
    public static ConfigurationManager getInstance() {
        return ConfigurationManagerHolder.INSTANCE;
    }
    
    private static class ConfigurationManagerHolder {

        private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    }  
    
//    /**
//     * Factory method della classe ConfigurationManager
//     * @return ConfigurationManager instance (singleton)
//     */
//    public static synchronized ConfigurationManager getInstance() {
//
//        if (singletonConfigurationManager == null) {
//            singletonConfigurationManager = new ConfigurationManager();
//        }
//
//        return singletonConfigurationManager;
//
//    }
//
//    private static ConfigurationManager singletonConfigurationManager = null;
    private Properties configProperties = null;
    private Properties serviceProperties = null;

    /**
     * @return the configProperties
     */
    public  Properties getConfigProperties() {
        return configProperties;
    }

    /**
     * @param aConfigProperties the configProperties to set
     */
    public  void setConfigProperties(Properties aConfigProperties) {
        configProperties = aConfigProperties;
    }

    /**
     * @return the serviceProperties
     */
    public  Properties getServiceProperties() {
        return serviceProperties;
    }

    /**
     * @param aServiceProperties the serviceProperties to set
     */
    public  void setServiceProperties(Properties aServiceProperties) {
        serviceProperties = aServiceProperties;
    }
}
