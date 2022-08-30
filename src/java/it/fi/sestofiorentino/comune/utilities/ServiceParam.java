/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.fi.sestofiorentino.comune.utilities;

/**
 *
 * @author emilios
 */
public class ServiceParam {

    public ServiceParam(String serviceID, String name, String key) {
        this.serviceID = serviceID;
        this.serviceName = name;
        this.serviceKey = key;
    }
    
    private String serviceID;
    private String serviceName;
    private String serviceKey;

    /**
     * @return the serviceID
     */
    public String getServiceID() {
        return serviceID;
    }

    /**
     * @param serviceID the serviceID to set
     */
    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceKey
     */
    public String getServiceKey() {
        return serviceKey;
    }

    /**
     * @param serviceKey the serviceKey to set
     */
    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }
    
}
