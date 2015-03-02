package com.homtrip.dao;

import java.util.List;
import java.util.Map;

import com.homtrip.common.Page;
import com.homtrip.model.CityHotelRelVO;



public interface ICityHotelRelDao {
	
    public List<CityHotelRelVO> findall();  
    
    public List<CityHotelRelVO> findByObject(CityHotelRelVO cityVO);

	public void insert(CityHotelRelVO vo);

	public Page<CityHotelRelVO> getPage(int start, int pageSize, Map<String, Object> searchMap);

	public void update(CityHotelRelVO vo);

	public void delete(int id);
}

