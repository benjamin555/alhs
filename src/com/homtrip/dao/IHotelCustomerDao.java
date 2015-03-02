package com.homtrip.dao;

import java.util.List;
import java.util.Map;

import com.homtrip.common.Page;
import com.homtrip.model.HotelJoinVO;

public interface IHotelCustomerDao {
    
      
    public List<HotelJoinVO> findAll();  
      
    public HotelJoinVO findById(String id);

	public Page<HotelJoinVO> getPage(int start, int size, Map<String, String> searchMap);  
	
	public boolean updateUnstatus(String guid); 
}
