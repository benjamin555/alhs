package com.homtrip.dao;

import java.util.List;
import java.util.Map;

import com.homtrip.common.Page;
import com.homtrip.model.HotelOrderVO;
import com.homtrip.model.HotelSummary;

/**
* @author �¼���
* @version ����ʱ�䣺2015-2-10 ����4:29:17
* @email benjaminchen555@gmail.com
*/
public interface IHotelOrderDao {

	Page<HotelOrderVO> getPage(int start, int pageSize, Map<String, Object> searchMap);

	HotelOrderVO getByGuid(String id);

	List<Map<String, Object>> getOrderCountMaps(HotelOrderVO vo);

	HotelSummary getSummary(String hotelName);

}
