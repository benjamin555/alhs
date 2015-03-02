package com.homtrip.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homtrip.common.Page;
import com.homtrip.dao.IHotelCustomerDao;
import com.homtrip.model.HotelJoinVO;
import com.homtrip.service.IHotelCustomerService;

@Service
@Transactional(readOnly=true)
public class HotelCustomerServiceImpl implements IHotelCustomerService  {
	
	    @Resource
	    private IHotelCustomerDao hotelCustomerDao;

		@Override
		@Transactional
		public List<HotelJoinVO> findAll() {
			// TODO Auto-generated method stub
			return hotelCustomerDao.findAll();
		}

		@Override
		@Transactional
		public HotelJoinVO findById(String id) {
			// TODO Auto-generated method stub
			return hotelCustomerDao.findById(id);
		}

		@Override
		@Transactional
		public Page<HotelJoinVO> getPage(int start, int size,
				Map<String, String> searchMap) {
			// TODO Auto-generated method stub
			return hotelCustomerDao.getPage(start, size, searchMap);
		}
		@Transactional
		@Override
		public boolean updateUnstatus(String guid) {
			// TODO Auto-generated method stub
			return hotelCustomerDao.updateUnstatus(guid);
		}  
	 

}
