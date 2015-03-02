package com.homtrip.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homtrip.common.Page;
import com.homtrip.dao.ICityHotelRelDao;
import com.homtrip.model.CityHotelRelVO;
import com.homtrip.service.ICityHotelRelService;

@Service
@Transactional(readOnly=true)
public class CityHotelRelServiceImpl implements ICityHotelRelService {
		/**
		 */
	    @Resource
	   private ICityHotelRelDao cityHotelRelDao;
	 
	 
	@Override
	@Transactional
	public List<CityHotelRelVO> findall() {
		// TODO Auto-generated method stub
		return cityHotelRelDao.findall();
	}

	@Override
	@Transactional
	public List<CityHotelRelVO> findByObject(CityHotelRelVO cityVO) {
		// TODO Auto-generated method stub
		return cityHotelRelDao.findByObject(cityVO);
	}

	@Override
	 @Transactional
	public void insert(CityHotelRelVO vo) {
		cityHotelRelDao.insert(vo);		
	}

	@Override
	public Page<CityHotelRelVO> getPage(int start, int pageSize, Map<String, Object> searchMap) {
		return cityHotelRelDao.getPage(start,pageSize,searchMap);
	}

	@Override
	 @Transactional
	public void update(CityHotelRelVO vo) {
		cityHotelRelDao.update(vo);
	}

	@Override
	 @Transactional
	public void delete(int id) {
		cityHotelRelDao.delete(id);
	}

}
