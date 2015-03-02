package com.homtrip.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.homtrip.model.HotelBankInfoVO;
import com.homtrip.model.HotelFacilitiesVO;
import com.homtrip.model.HotelGuidepostVO;
import com.homtrip.model.HotelJoinInfoVO;
import com.homtrip.model.HotelPhotosVO;
import com.homtrip.model.HotelPolicyInfo;
import com.homtrip.service.IHotelJoinInfoService;

/**
 * ���˿ͻ���ϸ��Ϣ
 * @author hh
 * 2015.2.9
 *
 */
@Controller
@RequestMapping("/hotelcustomerinfo")
public class HotelCustomerControllerInfo {
  
	private Logger  logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * service
	 * 
	 */
	@Resource
	private IHotelJoinInfoService hotelJoinInfoService;
	
	/**
	 * ���˿ͻ���ϸ��Ϣ��ʼ������
	 */
	@RequestMapping(value = "/getHotelCustomerInfo")
	public String getHotelCustomerInfo(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		request.setAttribute("hotelId", hotelId);
		return "/intentVipCustomerInfo1";
	}
	
	/**
	 * ���˿ͻ���ϸ��Ϣ��߲˵���ʼ������
	 */
	@RequestMapping(value = "/getHotelCustomerInfoLeft")
	public String getHotelCustomerInfoLeft(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		request.setAttribute("hotelId", hotelId);
		return "/intentVipCustomerInfo_left";
	}
	
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������-�Ƶ�����
	 */
	@RequestMapping(value = "/getHotelCustomerInfoRight")
	public ModelAndView getHotelCustomerInfoRight(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomerInfo_right");
			if (StringUtils.isNotBlank(hotelId)) {
				HotelJoinInfoVO vo = hotelJoinInfoService.findByid(hotelId);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
	
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ����
	 */
	@RequestMapping(value = "/getHotelCustomerintroduction")
	public ModelAndView getHotelCustomerintroduction(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_introduction");
			if (StringUtils.isNotBlank(hotelId)) {
				HotelJoinInfoVO vo = hotelJoinInfoService.findByid(hotelId);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
	
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ�ͼƬ
	 */
	@RequestMapping(value = "/getHotelCustomerpicture")
	public ModelAndView getHotelCustomerpicture(HttpServletRequest request,
			ModelMap modelMap,String hotelId,String guid,String type1,String type2,String type3,Integer status) throws Exception {
		    request.setAttribute("hotelId", hotelId);
		    System.out.println(hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_picture");
			if (StringUtils.isNotBlank(hotelId)) {
			    List<HotelPhotosVO> vo = hotelJoinInfoService.findPhotosByid(hotelId, guid, type1, type2, type3, status);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ���ʩ
	 * 
	 */
	@RequestMapping(value = "/getHotelCustomerFacilities")
	public ModelAndView getHotelCustomerFacilities(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_facilities");
			if (StringUtils.isNotBlank(hotelId)) {
				HotelFacilitiesVO vo = hotelJoinInfoService.findFacilitiesByid(hotelId);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ�����
	 * 
	 */
	@RequestMapping(value = "/getHotelCustomerPolicy")
	public ModelAndView getHotelCustomerPolicy(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_policy");
			if (StringUtils.isNotBlank(hotelId)) {
				HotelPolicyInfo vo = hotelJoinInfoService.getHotelCustomerPolicy(hotelId);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ귿��
	 * 
	 */
	@RequestMapping(value = "/getHotelCustomerRoomtype")
	public ModelAndView getHotelCustomerRoomtype(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_roomtype");
			if (StringUtils.isNotBlank(hotelId)) {
				/*HotelJoinInfoVO vo = hotelJoinInfoService.findByid(hotelId);
				modelAndView.addObject("hotelInfo", vo);*/
			}
			return modelAndView;
	}
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ귿��
	 * 
	 */
	@RequestMapping(value = "/getHotelCustomerRooms")
	public ModelAndView getHotelCustomerRooms(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_rooms");
			if (StringUtils.isNotBlank(hotelId)) {
				/*HotelJoinInfoVO vo = hotelJoinInfoService.findByid(hotelId);
				modelAndView.addObject("hotelInfo", vo);*/
			}
			return modelAndView;
	}
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�ر�·��
	 * 
	 */
	@RequestMapping(value = "/getHotelCustomerLandmarks")
	public ModelAndView getHotelCustomerLandmarks(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_landmarks");
			if (StringUtils.isNotBlank(hotelId)) {
				HotelGuidepostVO vo = hotelJoinInfoService.getHotelCustomerLandmarks(hotelId);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
	/**
	 * ���˿ͻ���ϸ��Ϣ�ұ߲˵���ʼ������--�Ƶ�������Ϣ
	 * 
	 */
	@RequestMapping(value = "/getHotelCustomerBank")
	public ModelAndView getHotelCustomerBank(HttpServletRequest request,
			ModelMap modelMap,String hotelId) throws Exception {
		    request.setAttribute("hotelId", hotelId);
			ModelAndView modelAndView = new ModelAndView("intentVipCustomer_bank");
			if (StringUtils.isNotBlank(hotelId)) {
				HotelBankInfoVO vo = hotelJoinInfoService.getHotelCustomerBank(hotelId);
				modelAndView.addObject("hotelInfo", vo);
			}
			return modelAndView;
	}
}
