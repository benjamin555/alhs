package com.homtrip.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HotelPhotosVO implements Serializable {

	private String guid;    //  �Ƶ���  
	private String bigpath ;   //ͼƬ·��  
	private String type1;   //  ͼƬ����  
	private String type2;  // ͼƬ����  
	private String type3 ;  //  ͼƬС��  
	private String times ;   //  �ϴ�ʱ��  
	private String size;     // ��С     
	private Integer status;  //  ״̬        
	private Integer id    ; 
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getBigpath() {
		return bigpath;
	}
	public void setBigpath(String bigpath) {
		this.bigpath = bigpath;
	}
	public String getType1() {
		return type1;
	}
	public void setType1(String type1) {
		this.type1 = type1;
	}
	public String getType2() {
		return type2;
	}
	public void setType2(String type2) {
		this.type2 = type2;
	}
	public String getType3() {
		return type3;
	}
	public void setType3(String type3) {
		this.type3 = type3;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
    
}
