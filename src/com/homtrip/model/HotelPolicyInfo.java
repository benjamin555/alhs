package com.homtrip.model;

public class HotelPolicyInfo {


private String uid ;            // 酒店编号        
private Integer inandout;        // 入住和离店     
private Integer isstatus   ;     //  取消政策        
private String dietary_arrangement   ; //  膳食安排        
private Integer isPet   ;            //是否可带宠物  
private String onlinePayment;       // 线上支付        
private String mode_payment;        // 付款方式        
private String requirement  ;    // 入住要求        
private Integer id       ;         //
public String getUid() {
	return uid;
}
public void setUid(String uid) {
	this.uid = uid;
}
public Integer getInandout() {
	return inandout;
}
public void setInandout(Integer inandout) {
	this.inandout = inandout;
}
public Integer getIsstatus() {
	return isstatus;
}
public void setIsstatus(Integer isstatus) {
	this.isstatus = isstatus;
}
public String getDietary_arrangement() {
	return dietary_arrangement;
}
public void setDietary_arrangement(String dietary_arrangement) {
	this.dietary_arrangement = dietary_arrangement;
}
public Integer getIsPet() {
	return isPet;
}
public void setIsPet(Integer isPet) {
	this.isPet = isPet;
}
public String getOnlinePayment() {
	return onlinePayment;
}
public void setOnlinePayment(String onlinePayment) {
	this.onlinePayment = onlinePayment;
}
public String getMode_payment() {
	return mode_payment;
}
public void setMode_payment(String mode_payment) {
	this.mode_payment = mode_payment;
}
public String getRequirement() {
	return requirement;
}
public void setRequirement(String requirement) {
	this.requirement = requirement;
}
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}

}