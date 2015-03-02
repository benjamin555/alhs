package com.homtrip.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.homtrip.common.Page;
import com.homtrip.dao.IHotelCustomerDao;
import com.homtrip.model.HotelJoinVO;

@Repository
public class HotelCustomerDaoImpl implements IHotelCustomerDao {



	private static final String SELECTBYID = "selectcustomerByGuid";

	private static final String SELECTPAGE = "selectcustomerPage";

	private static final String SELECTCOUNT = "selectcustomerCount";
	
	private static final String UPDATE = "updatecustomer";
	
	private static final String selectcustomerAll = "selectcustomerAll"; 

	private SqlSessionTemplate sqlSession;

	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}



	@Override
	public List<HotelJoinVO> findAll() {
		// TODO Auto-generated method stub  
		String sql = this.getStatementId(HotelJoinVO.class, selectcustomerAll);
		List<HotelJoinVO> list = sqlSession.selectList(sql);
		return list;
	}

	@Override
	public HotelJoinVO findById(String id) {
		// TODO Auto-generated method stub  
		String sql = this.getStatementId(HotelJoinVO.class, SELECTBYID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("guid", id);
		HotelJoinVO vo= sqlSession.selectOne(sql, m);
		return vo;
	}

	/**   
	 * 映射sqlid 
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
	public boolean updateUnstatus(String guid) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelJoinVO.class, UPDATE);
		this.sqlSession.update(sql, guid);
		return true;
	}
}
