package com.homtrip.service;

import java.util.List;

import com.homtrip.model.HotelBankInfoVO;
import com.homtrip.model.HotelFacilitiesVO;
import com.homtrip.model.HotelGuidepostVO;
import com.homtrip.model.HotelJoinInfoVO;
import com.homtrip.model.HotelPhotosVO;
import com.homtrip.model.HotelPolicyInfo;

public interface IHotelJoinInfoService {
	/**
	 * 酒店详细信息
	 * @param hotelId
	 * @return
	 */
	public HotelJoinInfoVO findByid(String hotelId);
	
	/**
	 * 酒店图片
	 * @param hotelId
	 * @return
	 */
	public List<HotelPhotosVO> findPhotosByid(String hotelId,String guid,String type1,String type2,String type3,Integer status);

	/**
	 * 酒店设施信息
	 * @param hotelId
	 * @return
	 */
	public HotelFacilitiesVO findFacilitiesByid(String hotelId);

	/**
	 * 酒店政策
	 * @param hotelId
	 * @return
	 */
	public HotelPolicyInfo getHotelCustomerPolicy(String hotelId);

	/**
	 * 酒店交通
	 * @param hotelId
	 * @return
	 */
	public HotelGuidepostVO  getHotelCustomerLandmarks(String hotelId);
	
	/**
	 * 酒店开户银行信息
	 * @param hotelId
	 * @return
	 */
	public HotelBankInfoVO  getHotelCustomerBank(String hotelId);
}


