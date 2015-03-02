package com.homtrip.service;

import java.util.Map;

import com.homtrip.common.Page;
import com.homtrip.model.HotelOrderVO;
import com.homtrip.model.HotelOrderVO.Status;
import com.homtrip.model.HotelSummary;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-10 下午4:25:44
* @email benjaminchen555@gmail.com
*/
public interface IHotelOrderService {

	Page<HotelOrderVO> getPage(int start, int pageSize, Map<String, Object> searchMap);


	HotelOrderVO getByGuid(String id);


	Map<Status, Long> getOrderCountMap(HotelOrderVO vo);


	HotelSummary getSummary(String hotelName);

}
