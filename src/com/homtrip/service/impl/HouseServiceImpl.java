package com.homtrip.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homtrip.dao.IHouseDao;
import com.homtrip.model.HouseVO;
import com.homtrip.service.IHouseService;

/**
* @author �¼���
* @version ����ʱ�䣺2015-3-2 ����11:01:31
* @email benjaminchen555@gmail.com
*/
@Service
public class HouseServiceImpl implements IHouseService {
	@Autowired
	private IHouseDao dao;

	@Override
	public HouseVO getById(String guid) {
		return dao.getById(guid);
	}

}
