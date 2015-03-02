package com.homtrip.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.homtrip.common.Page;
import com.homtrip.dao.ICityHotelRelDao;
import com.homtrip.model.CityHotelRelVO;
@Repository
public class CityHotelRelDaoImpl implements ICityHotelRelDao {
	
	private static final String DELETE_BY_PRIMARY_KEY = "deleteByPrimaryKey";

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String FINDALL = "selectCityAll";
	
	private static final String FINDOBJECT = "selectCityBy"; 
	
	private static final String SELECTPAGE = "selectPage";

	private static final String SELECTCOUNT = "selectCount";

	private static final String INSERT = "insert";

	private static final String UPDATE = "update";

	
	private SqlSessionTemplate sqlSession;
	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	@Override
	public List<CityHotelRelVO> findall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CityHotelRelVO> findByObject(CityHotelRelVO cityVO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(CityHotelRelVO vo) {
		String sql = this.getStatementId(CityHotelRelVO.class, INSERT);
		this.sqlSession.insert(sql, vo);
	}
	
	private String getStatementId(Class entityClass, String suffix) {
		String sqlStr = entityClass.getName() + "." + suffix;
		return sqlStr;
	}

	@Override
	public Page<CityHotelRelVO> getPage(int start, int pageSize, Map<String, Object> searchMap) {
		String sql = this.getStatementId(CityHotelRelVO.class, SELECTPAGE);
		Map<String, Object> values = new HashMap<String, Object>(); 
		values.put("start", start);
		values.put("size", pageSize);
		values.putAll(searchMap);
		logger.info("values:{}",values);
		List<CityHotelRelVO> list = sqlSession.selectList(sql, values);
		Page<CityHotelRelVO> page = new Page<CityHotelRelVO>(start,pageSize);
		page.setResult(list);
		long totalCount = 0;
		String sql2 = this.getStatementId(CityHotelRelVO.class, SELECTCOUNT);
		totalCount = sqlSession.selectOne(sql2,values);
		page.setTotalCount(totalCount);
		return page;
	}

	@Override
	public void update(CityHotelRelVO vo) {
		String sql = this.getStatementId(CityHotelRelVO.class, UPDATE);
		this.sqlSession.update(sql, vo);
	}

	@Override
	public void delete(int id) {
		String sql = this.getStatementId(CityHotelRelVO.class, DELETE_BY_PRIMARY_KEY);
		this.sqlSession.delete(sql, id);
	}

}
