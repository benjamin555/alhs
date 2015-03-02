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
import com.homtrip.dao.IHotelJoinDao;
import com.homtrip.model.HotelJoinVO;

@Repository
public class HotelJoinDaoImpl implements IHotelJoinDao {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String INSERT = "insert";

	private static final String UPDATE = "update";

	private static final String DELETE = "delete";

	private static final String SELECTALL = "selectAll";

	private static final String SELECTBYID = "selectById";

	private static final String SELECTPAGE = "selectPage";

	private static final String SELECTCOUNT = "selectCount";

	private static final String SELECT_BY_GUID = "selectByGuid";

	private SqlSessionTemplate sqlSession;

	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub  
		String sql = this.getStatementId(HotelJoinVO.class, DELETE);
		sqlSession.delete(sql, id);
		return true;
	}

	@Override
	public List<HotelJoinVO> findAll() {
		// TODO Auto-generated method stub  
		String sql = this.getStatementId(HotelJoinVO.class, SELECTALL);
		List<HotelJoinVO> list = sqlSession.selectList(sql);
		return list;
	}
	@Override
	@SuppressWarnings("static-access")
	public boolean insert(HotelJoinVO stu) {
		// TODO Auto-generated method stub  
		String sql = this.getStatementId(HotelJoinVO.class, INSERT);
		this.sqlSession.insert(sql, stu);
		return true;
	}

	@Override
	public boolean update(HotelJoinVO stu) {
		// TODO Auto-generated method stub  
		String sql = this.getStatementId(HotelJoinVO.class, UPDATE);
		this.sqlSession.update(sql, stu);
		return true;
	}

	/**   
	*/
	private String getStatementId(Class entityClass, String suffix) {
		String sqlStr = entityClass.getName() + "." + suffix;
		System.out.println("getStatementId:" + sqlStr);
		return sqlStr;
	}
	
	@Override
	public Page<HotelJoinVO> getPage(int start, int size, Map<String, String> searchMap) {

		String sql = this.getStatementId(HotelJoinVO.class, SELECTPAGE);
		Map<String, Object> values = new HashMap<String, Object>(); 
		
		values.put("start", start);
		values.put("size", size);
		values.putAll(searchMap);
		logger.info("values:{}",values);
		List<HotelJoinVO> list = sqlSession.selectList(sql, values);
		Page<HotelJoinVO> page = new Page<HotelJoinVO>(start,size);
		page.setResult(list);
		long totalCount = 0;
		String sql2 = this.getStatementId(HotelJoinVO.class, SELECTCOUNT);
		totalCount = sqlSession.selectOne(sql2,values);
		page.setTotalCount(totalCount);
		return page;
	}

	@Override
	public HotelJoinVO findByGuid(String guid) {
		String sql = this.getStatementId(HotelJoinVO.class, SELECT_BY_GUID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("guid", guid);
		HotelJoinVO vo= sqlSession.selectOne(sql, m);
		return vo;
	}
	@Override
	public boolean updatestatus(String guid) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelJoinVO.class, UPDATE);
		this.sqlSession.update(sql, guid);
		return true;
	}
}
