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
	 * �Ƶ���ϸ��Ϣ
	 * @param hotelId
	 * @return
	 */
	public HotelJoinInfoVO findByid(String hotelId);
	
	/**
	 * �Ƶ�ͼƬ
	 * @param hotelId
	 * @return
	 */
	public List<HotelPhotosVO> findPhotosByid(String hotelId,String guid,String type1,String type2,String type3,Integer status);

	/**
	 * �Ƶ���ʩ��Ϣ
	 * @param hotelId
	 * @return
	 */
	public HotelFacilitiesVO findFacilitiesByid(String hotelId);

	/**
	 * �Ƶ�����
	 * @param hotelId
	 * @return
	 */
	public HotelPolicyInfo getHotelCustomerPolicy(String hotelId);

	/**
	 * �Ƶ꽻ͨ
	 * @param hotelId
	 * @return
	 */
	public HotelGuidepostVO  getHotelCustomerLandmarks(String hotelId);
	
	/**
	 * �Ƶ꿪��������Ϣ
	 * @param hotelId
	 * @return
	 */
	public HotelBankInfoVO  getHotelCustomerBank(String hotelId);
}


