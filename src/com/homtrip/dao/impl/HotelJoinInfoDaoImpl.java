package com.homtrip.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.homtrip.dao.IHotelJoinInfoDao;
import com.homtrip.model.HotelBankInfoVO;
import com.homtrip.model.HotelFacilitiesVO;
import com.homtrip.model.HotelGuidepostVO;
import com.homtrip.model.HotelJoinInfoVO;
import com.homtrip.model.HotelPhotosVO;
import com.homtrip.model.HotelPolicyInfo;

@Repository
public class HotelJoinInfoDaoImpl implements IHotelJoinInfoDao {

	private static final String SELECTBYID = "selectByid";
	private static final String SELECTPHOTOSBYID = "selectPhotosByid";
	private static final String SELECTFacilitiesBYID = "SELECTFacilitiesBYID";
	private static final String SELECTPolicyYID = "SELECTPolicyYID";
	private static final String SELECGuidepostID = "SELECGuidepostID";
	private static final String SELECBankInfoID = "SELECBankInfoID";
	
	private SqlSessionTemplate sqlSession;
	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	@Override
	public HotelJoinInfoVO findByid(String hotelId) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelJoinInfoVO.class, SELECTBYID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("hotelId", hotelId);
		HotelJoinInfoVO vo= sqlSession.selectOne(sql, m);
		System.out.println("huanghao11111111111"+vo);
		return vo;
	}

	/** 
	 * 
	*/
	private String getStatementId(Class entityClass, String suffix) {
		String sqlStr = entityClass.getName() + "." + suffix;
		System.out.println("getStatementId:" + sqlStr);
		return sqlStr;
	}

	@Override
	public List<HotelPhotosVO> findPhotosByid(String hotelId, String guid,
			String type1, String type2, String type3, Integer status) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelPhotosVO.class, SELECTPHOTOSBYID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("hotelId", hotelId);
		List<HotelPhotosVO> vo= sqlSession.selectList(sql, m);
		System.out.println("huanghao22222222"+vo);
		return vo;
	}

	@Override
	public HotelFacilitiesVO findFacilitiesByid(String hotelId) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelFacilitiesVO.class, SELECTFacilitiesBYID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("hotelId", hotelId);
		HotelFacilitiesVO vo= sqlSession.selectOne(sql, m);
		System.out.println("huanghao3333333333"+vo);
		return vo;
	}

	@Override
	public HotelPolicyInfo getHotelCustomerPolicy(String hotelId) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelPolicyInfo.class, SELECTPolicyYID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("hotelId", hotelId);
		HotelPolicyInfo vo= sqlSession.selectOne(sql, m);
		System.out.println("huanghao3333333333"+vo);
		return vo;
	}

	@Override
	public HotelGuidepostVO getHotelCustomerLandmarks(String hotelId) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelGuidepostVO.class, SELECGuidepostID);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("hotelId", hotelId);
		HotelGuidepostVO vo= sqlSession.selectOne(sql, m);
		System.out.println("huanghao3333333333"+vo);
		return vo;
	}

	@Override
	public HotelBankInfoVO getHotelCustomerBank(String hotelId) {
		// TODO Auto-generated method stub
		String sql = this.getStatementId(HotelBankInfoVO.class,SELECBankInfoID );
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("hotelId", hotelId);
		HotelBankInfoVO vo= sqlSession.selectOne(sql, m);
		System.out.println("huanghao3333333333"+vo);
		return vo;
	}
}
