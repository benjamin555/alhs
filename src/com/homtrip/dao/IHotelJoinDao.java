package com.homtrip.dao;

import java.util.List;
import java.util.Map;

import com.homtrip.common.Page;
import com.homtrip.model.HotelJoinVO;

public interface IHotelJoinDao {
	public boolean insert(HotelJoinVO stu);

	public boolean delete(int id);

	public boolean update(HotelJoinVO stu);

	public List<HotelJoinVO> findAll();

	public Page<HotelJoinVO> getPage(int start, int size, Map<String, String> searchMap); 

	public HotelJoinVO findByGuid(String guid);

	public boolean updatestatus(String guid);

}
