package com.homtrip.model;



import java.io.Serializable;

@SuppressWarnings("serial")
public class CityHotelRelVO extends BaseEntity implements Serializable {
	private Integer id  ;     
	private String province;
	private String city;   
	private String type;    
	private String subway ;    
	private String landmark;  
	private String longitude; 
	private String latitude;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubway() {
		return subway;
	}
	public void setSubway(String subway) {
		this.subway = subway;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public enum Type{
		//������,��ҵ��,����/��,����,��У,չ����,��Ȫ,����԰,ҽԺ,��ѩ��,������
		XZQ("������"),SYQ("��ҵ��"),JC("����/��"),JQ("����,"),GX("��У"),ZLG("չ����"),WQ("��Ȫ"),YLY("����԰"),YY("ҽԺ"),HXC("��ѩ��"),DTX("������");
		private String text;

		private Type(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		
	}

}
