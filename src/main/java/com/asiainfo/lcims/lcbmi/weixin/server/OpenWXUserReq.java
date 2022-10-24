/**
 * OpenWXUserReq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.asiainfo.lcims.lcbmi.weixin.server;

public class OpenWXUserReq  implements java.io.Serializable {
    private java.lang.String openid;

    private java.lang.String phone;

    private java.lang.String registerid;

    private java.lang.String reqNo;

    private java.lang.String timeStamp;

    public OpenWXUserReq() {
    }

    public OpenWXUserReq(
           java.lang.String openid,
           java.lang.String phone,
           java.lang.String registerid,
           java.lang.String reqNo,
           java.lang.String timeStamp) {
           this.openid = openid;
           this.phone = phone;
           this.registerid = registerid;
           this.reqNo = reqNo;
           this.timeStamp = timeStamp;
    }


    /**
     * Gets the openid value for this OpenWXUserReq.
     * 
     * @return openid
     */
    public java.lang.String getOpenid() {
        return openid;
    }


    /**
     * Sets the openid value for this OpenWXUserReq.
     * 
     * @param openid
     */
    public void setOpenid(java.lang.String openid) {
        this.openid = openid;
    }


    /**
     * Gets the phone value for this OpenWXUserReq.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this OpenWXUserReq.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the registerid value for this OpenWXUserReq.
     * 
     * @return registerid
     */
    public java.lang.String getRegisterid() {
        return registerid;
    }


    /**
     * Sets the registerid value for this OpenWXUserReq.
     * 
     * @param registerid
     */
    public void setRegisterid(java.lang.String registerid) {
        this.registerid = registerid;
    }


    /**
     * Gets the reqNo value for this OpenWXUserReq.
     * 
     * @return reqNo
     */
    public java.lang.String getReqNo() {
        return reqNo;
    }


    /**
     * Sets the reqNo value for this OpenWXUserReq.
     * 
     * @param reqNo
     */
    public void setReqNo(java.lang.String reqNo) {
        this.reqNo = reqNo;
    }


    /**
     * Gets the timeStamp value for this OpenWXUserReq.
     * 
     * @return timeStamp
     */
    public java.lang.String getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this OpenWXUserReq.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(java.lang.String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OpenWXUserReq)) return false;
        OpenWXUserReq other = (OpenWXUserReq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.openid==null && other.getOpenid()==null) || 
             (this.openid!=null &&
              this.openid.equals(other.getOpenid()))) &&
            ((this.phone==null && other.getPhone()==null) || 
             (this.phone!=null &&
              this.phone.equals(other.getPhone()))) &&
            ((this.registerid==null && other.getRegisterid()==null) || 
             (this.registerid!=null &&
              this.registerid.equals(other.getRegisterid()))) &&
            ((this.reqNo==null && other.getReqNo()==null) || 
             (this.reqNo!=null &&
              this.reqNo.equals(other.getReqNo()))) &&
            ((this.timeStamp==null && other.getTimeStamp()==null) || 
             (this.timeStamp!=null &&
              this.timeStamp.equals(other.getTimeStamp())));
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
        if (getOpenid() != null) {
            _hashCode += getOpenid().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        if (getRegisterid() != null) {
            _hashCode += getRegisterid().hashCode();
        }
        if (getReqNo() != null) {
            _hashCode += getReqNo().hashCode();
        }
        if (getTimeStamp() != null) {
            _hashCode += getTimeStamp().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OpenWXUserReq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://server.weixin.lcbmi.lcims.asiainfo.com/", "openWXUserReq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("openid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "openid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registerid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "registerid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reqNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reqNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
