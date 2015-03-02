package com.homtrip.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homtrip.common.Page;
import com.homtrip.dao.IHotelOrderDao;
import com.homtrip.model.HotelOrderVO;
import com.homtrip.model.HotelOrderVO.Status;
import com.homtrip.model.HotelSummary;
import com.homtrip.service.IHotelOrderService;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-10 下午4:27:50
* @email benjaminchen555@gmail.com
*/
@Service
@Transactional(readOnly=true)
public class HotelOrderServiceImpl implements IHotelOrderService {
	@Autowired
	private IHotelOrderDao dao;
	@Override
	public Page<HotelOrderVO> getPage(int start, int pageSize, Map<String, Object> searchMap) {
		return dao.getPage(start,pageSize,searchMap);
	}
	@Override
	public HotelOrderVO getByGuid(String id) {
		return dao.getByGuid(id);
	}
	@Override
	public Map<Status, Long> getOrderCountMap(HotelOrderVO vo) {
		Map<Status, Long> m =new  HashMap<Status, Long>();
		List<Map<String, Object>>ms = dao.getOrderCountMaps(vo);
		for (Map<String, Object> map : ms) {
			String status = (String) map.get("STATUS");
			Long cnt = (Long) map.get("cnt");
			m.put(Status.getByKey(status), cnt);
		}
		
		return m ;
	}
	@Override
	public HotelSummary getSummary(String hotelName) {
		return dao.getSummary(hotelName);
	}

}
