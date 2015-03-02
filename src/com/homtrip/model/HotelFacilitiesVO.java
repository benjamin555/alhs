package com.homtrip.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HotelFacilitiesVO implements Serializable {
	private String guid    ;               //酒店编号  
	private String network_facility;      //  网络设施  
	private String car_park;               //  停车场     
	private String generic_facility;      //  通用设施  
	private String event_facilities;       //  活动设施  
	private String service_project;        //  服务项目  
	private String guest_room_facilities;  //  客房设施  
	private Integer id  ;    
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getNetwork_facility() {
		return network_facility;
	}
	public void setNetwork_facility(String network_facility) {
		this.network_facility = network_facility;
	}
	public String getCar_park() {
		return car_park;
	}
	public void setCar_park(String car_park) {
		this.car_park = car_park;
	}
	public String getGeneric_facility() {
		return generic_facility;
	}
	public void setGeneric_facility(String generic_facility) {
		this.generic_facility = generic_facility;
	}
	public String getEvent_facilities() {
		return event_facilities;
	}
	public void setEvent_facilities(String event_facilities) {
		this.event_facilities = event_facilities;
	}
	public String getService_project() {
		return service_project;
	}
	public void setService_project(String service_project) {
		this.service_project = service_project;
	}
	public String getGuest_room_facilities() {
		return guest_room_facilities;
	}
	public void setGuest_room_facilities(String guest_room_facilities) {
		this.guest_room_facilities = guest_room_facilities;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
                
}
