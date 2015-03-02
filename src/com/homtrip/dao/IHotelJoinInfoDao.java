package com.homtrip.dao;

import java.util.List;

import com.homtrip.model.HotelBankInfoVO;
import com.homtrip.model.HotelFacilitiesVO;
import com.homtrip.model.HotelGuidepostVO;
import com.homtrip.model.HotelJoinInfoVO;
import com.homtrip.model.HotelPhotosVO;
import com.homtrip.model.HotelPolicyInfo;

public interface IHotelJoinInfoDao {
	
	public HotelJoinInfoVO findByid(String hotelId);
	
	public List<HotelPhotosVO> findPhotosByid(String hotelId, String guid,
			String type1, String type2, String type3, Integer status);
	
	public HotelFacilitiesVO findFacilitiesByid(String hotelId);
	
	public HotelPolicyInfo getHotelCustomerPolicy(String hotelId);
	
	public HotelGuidepostVO getHotelCustomerLandmarks(String hotelId);
	
	public HotelBankInfoVO getHotelCustomerBank(String hotelId);
}
