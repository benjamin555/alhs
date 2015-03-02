package com.homtrip.dao;

import com.homtrip.model.HouseVO;

/**
* @author 陈嘉镇
* @version 创建时间：2015-3-2 上午11:02:07
* @email benjaminchen555@gmail.com
*/
public interface IHouseDao {

	HouseVO getById(String guid);

}
