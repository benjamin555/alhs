package com.homtrip.dao.impl;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.homtrip.dao.IHouseDao;
import com.homtrip.mapper.HouseVOMapper;
import com.homtrip.model.HouseVO;

/**
* @author �¼���
* @version ����ʱ�䣺2015-3-2 ����11:03:35
* @email benjaminchen555@gmail.com
*/
@Repository
public class HouseDaoImpl implements IHouseDao {
	private SqlSessionTemplate sqlSession;
	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
	@Override
	public HouseVO getById(String guid) {
		HouseVOMapper m = sqlSession.getMapper(HouseVOMapper.class);
		return m.selectByPrimaryKey(guid);
	}

}
