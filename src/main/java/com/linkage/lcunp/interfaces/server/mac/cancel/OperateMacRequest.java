/**
 * OperateMacRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.linkage.lcunp.interfaces.server.mac.cancel;

public class OperateMacRequest  implements java.io.Serializable {
    private java.lang.String ecCode;

    private java.lang.String password;

    private int sourcetype;

    private java.lang.String timeStamp;

    private java.lang.String userIp;

    private java.lang.String userName;

    public OperateMacRequest() {
    }

    public OperateMacRequest(
           java.lang.String ecCode,
           java.lang.String password,
           int sourcetype,
           java.lang.String timeStamp,
           java.lang.String userIp,
           java.lang.String userName) {
           this.ecCode = ecCode;
           this.password = password;
           this.sourcetype = sourcetype;
           this.timeStamp = timeStamp;
           this.userIp = userIp;
           this.userName = userName;
    }


    /**
     * Gets the ecCode value for this OperateMacRequest.
     * 
     * @return ecCode
     */
    public java.lang.String getEcCode() {
        return ecCode;
    }


    /**
     * Sets the ecCode value for this OperateMacRequest.
     * 
     * @param ecCode
     */
    public void setEcCode(java.lang.String ecCode) {
        this.ecCode = ecCode;
    }


    /**
     * Gets the password value for this OperateMacRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this OperateMacRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the sourcetype value for this OperateMacRequest.
     * 
     * @return sourcetype
     */
    public int getSourcetype() {
        return sourcetype;
    }


    /**
     * Sets the sourcetype value for this OperateMacRequest.
     * 
     * @param sourcetype
     */
    public void setSourcetype(int sourcetype) {
        this.sourcetype = sourcetype;
    }


    /**
     * Gets the timeStamp value for this OperateMacRequest.
     * 
     * @return timeStamp
     */
    public java.lang.String getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this OperateMacRequest.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(java.lang.String timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * Gets the userIp value for this OperateMacRequest.
     * 
     * @return userIp
     */
    public java.lang.String getUserIp() {
        return userIp;
    }


    /**
     * Sets the userIp value for this OperateMacRequest.
     * 
     * @param userIp
     */
    public void setUserIp(java.lang.String userIp) {
        this.userIp = userIp;
    }


    /**
     * Gets the userName value for this OperateMacRequest.
     * 
     * @return userName
     */
    public java.lang.String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this OperateMacRequest.
     * 
     * @param userName
     */
    public void setUserName(java.lang.String userName) {
        this.userName = userName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OperateMacRequest)) return false;
        OperateMacRequest other = (OperateMacRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ecCode==null && other.getEcCode()==null) || 
             (this.ecCode!=null &&
              this.ecCode.equals(other.getEcCode()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            this.sourcetype == other.getSourcetype() &&
            ((this.timeStamp==null && other.getTimeStamp()==null) || 
             (this.timeStamp!=null &&
              this.timeStamp.equals(other.getTimeStamp()))) &&
            ((this.userIp==null && other.getUserIp()==null) || 
             (this.userIp!=null &&
              this.userIp.equals(other.getUserIp()))) &&
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getEcCode() != null) {
            _hashCode += getEcCode().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        _hashCode += getSourcetype();
        if (getTimeStamp() != null) {
            _hashCode += getTimeStamp().hashCode();
        }
        if (getUserIp() != null) {
            _hashCode += getUserIp().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OperateMacRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "OperateMacRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "ecCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourcetype");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "sourcetype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userIp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "userIp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cancel.mac.server.interfaces.lcunp.linkage.com", "userName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
