package com.homtrip.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homtrip.dao.IHotelJoinInfoDao;
import com.homtrip.model.HotelBankInfoVO;
import com.homtrip.model.HotelFacilitiesVO;
import com.homtrip.model.HotelGuidepostVO;
import com.homtrip.model.HotelJoinInfoVO;
import com.homtrip.model.HotelPhotosVO;
import com.homtrip.model.HotelPolicyInfo;
import com.homtrip.service.IHotelJoinInfoService;

@Service
@Transactional(readOnly=true)
public class HotelJoinInfoServiceImpl implements IHotelJoinInfoService {
	
    @Resource
    private IHotelJoinInfoDao hotelJoinInfoDao;  
    

	@Transactional
	@Override
	public HotelJoinInfoVO findByid(String hotelId) {
		// TODO Auto-generated method stub
		return hotelJoinInfoDao.findByid(hotelId);
	}


	@Override
	public List<HotelPhotosVO> findPhotosByid(String hotelId, String guid,
			String type1, String type2, String type3, Integer status) {
		// TODO Auto-generated method stub
		return hotelJoinInfoDao.findPhotosByid(hotelId, guid, type1, type2, type3, status);
	}


	@Override
	public HotelFacilitiesVO findFacilitiesByid(String hotelId) {
		// TODO Auto-generated method stub
		return hotelJoinInfoDao.findFacilitiesByid(hotelId);
	}


	@Override
	public HotelPolicyInfo getHotelCustomerPolicy(String hotelId) {
		// TODO Auto-generated method stub
		return  hotelJoinInfoDao.getHotelCustomerPolicy(hotelId);
	}


	@Override
	public HotelGuidepostVO getHotelCustomerLandmarks(String hotelId) {
		// TODO Auto-generated method stub
		return hotelJoinInfoDao.getHotelCustomerLandmarks(hotelId);
	}


	@Override
	public HotelBankInfoVO getHotelCustomerBank(String hotelId) {
		// TODO Auto-generated method stub
		return hotelJoinInfoDao.getHotelCustomerBank(hotelId);
	}
	
}
