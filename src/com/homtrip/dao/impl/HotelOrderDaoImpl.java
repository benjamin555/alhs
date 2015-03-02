package com.homtrip.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.homtrip.common.Page;
import com.homtrip.dao.IHotelOrderDao;
import com.homtrip.mapper.HotelOrderVOMapper;
import com.homtrip.model.HotelOrderVO;
import com.homtrip.model.HotelOrderVO.Status;
import com.homtrip.model.HotelOrderVOExample;
import com.homtrip.model.HotelOrderVOExample.Criteria;
import com.homtrip.model.HotelSummary;
import com.homtrip.model.HouseVO;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-10 下午4:30:04
* @email benjaminchen555@gmail.com
*/
@Repository
public class HotelOrderDaoImpl implements IHotelOrderDao {

	private SqlSessionTemplate sqlSession;
	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	@Override
	public Page<HotelOrderVO> getPage(int start, int pageSize, Map<String, Object> searchMap) {
		HotelOrderVOMapper mapper = sqlSession.getMapper(HotelOrderVOMapper.class);
	    HotelOrderVOExample e =new HotelOrderVOExample();
	    Page<HotelOrderVO> page = new Page<HotelOrderVO>(start,pageSize);
	    e.setPage(page);
	    Criteria c =e.createCriteria();
	    HotelOrderVO model = (HotelOrderVO) searchMap.get("model");
	   
	    
	    if (searchMap.get("sqdh")!=null) {
	    	c.andSqdhEqualTo(searchMap.get("sqdh").toString());
		}
	    Object ddzt = searchMap.get("ddzt");
		if(ddzt!=null){
	    	if (Status.noShow.getKey().equals(ddzt)) {
				c.andNoshow();
			}else {
				c.andDdztEqualTo(ddzt.toString());
			}
	    	
	    }
	    if(searchMap.get("xm")!=null){
	    	c.andXmEqualTo(searchMap.get("xm").toString());
	    }
	    if(searchMap.get("lxdh")!=null){
	    	c.andLxdhEqualTo(searchMap.get("lxdh").toString());
	    }
	    if(searchMap.get("rzrqFrom")!=null){
	    	c.andRzrqGreaterThanOrEqualTo(model.getRzrqFrom());
	    }
	  
	    if(searchMap.get("rzrqTo")!=null){
	    	c.andRzrqLessThanOrEqualTo(model.getRzrqTo());
	    }
	    
	    if(searchMap.get("tfrqFrom")!=null){
	    	c.andTfrqGreaterThanOrEqualTo(model.getTfrqFrom());
	    }
	    if(searchMap.get("tfrqTo")!=null){
	    	c.andTfrqLessThanOrEqualTo(model.getTfrqTo());
	    }
	    
	    if(searchMap.get("rzts")!=null){
	    	c.andRztsEqualTo(model.getRzts());
	    }
	    
	    //订单来源
	    if(StringUtils.isNotBlank(model.getDdsx())){
	    	c.andDdsxEqualTo(model.getDdsx());
	    }
	    
	    HouseVO houseVO = model.getHouse();
	    addHouseCriteria(c, houseVO);
	    
	    
	    
		List<HotelOrderVO> list = mapper.selectByExample(e);
		page.setResult(list);
		return page;
	}

	private void addHouseCriteria(Criteria c, HouseVO houseVO) {
		if(houseVO!=null){
	    	if (StringUtils.isNotBlank(houseVO.getFx())) {
	    		c.andHouseFx(houseVO.getFx());
			}
	    	if (houseVO.getDong()!=null) {
	    		c.andHouseDong(houseVO.getDong());
			}
	    	if (houseVO.getLouceng()!=null) {
	    		c.andHouseLouceng(houseVO.getLouceng());
			}
	    	if (houseVO.getFjh()!=null) {
	    		c.andHouseFjh(houseVO.getFjh());
			}
	    	
	    	if (StringUtils.isNotBlank(houseVO.getHotelName())) {
	    		c.andHotelName(houseVO.getHotelName());
			}
	    	
	    }
	}

	@Override
	public HotelOrderVO getByGuid(String id) {
		HotelOrderVOMapper mapper = sqlSession.getMapper(HotelOrderVOMapper.class);
		return  mapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Map<String, Object>> getOrderCountMaps(HotelOrderVO vo) {
		HotelOrderVOMapper mapper = sqlSession.getMapper(HotelOrderVOMapper.class);
		HotelOrderVOExample e =new HotelOrderVOExample();
		 Criteria c =e.createCriteria();
		 
		 if (StringUtils.isNotBlank(vo.getSqdh())) {
		    	c.andSqdhEqualTo(vo.getSqdh());
			}
		    if(StringUtils.isNotBlank(vo.getXm())){
		    	c.andXmEqualTo(vo.getXm());
		    }
		    if(StringUtils.isNotBlank(vo.getLxdh())){
		    	c.andLxdhEqualTo(vo.getLxdh());
		    }
		    if(vo.getRzrqFrom()!=null){
		    	c.andRzrqGreaterThanOrEqualTo(vo.getRzrqFrom());
		    }
		  
		    if(vo.getRzrqTo()!=null){
		    	c.andRzrqLessThanOrEqualTo(vo.getRzrqTo());
		    }
		    
		    if(vo.getTfrqFrom()!=null){
		    	c.andTfrqGreaterThanOrEqualTo(vo.getTfrqFrom());
		    }
		    if(vo.getTfrqTo()!=null){
		    	c.andTfrqLessThanOrEqualTo(vo.getTfrqTo());
		    }
		    
		    if(vo.getRzts()!=null){
		    	c.andRztsEqualTo(vo.getRzts());
		    }
		    
		    //订单来源
		    if(StringUtils.isNotBlank(vo.getDdsx())){
		    	c.andDdsxEqualTo(vo.getDdsx());
		    }
		 
		 addHouseCriteria(c, vo.getHouse());
		return  mapper.selectOrderCount(e);
	}

	@Override
	public HotelSummary getSummary(String hotelName) {
		HotelOrderVOMapper mapper = sqlSession.getMapper(HotelOrderVOMapper.class);
		return  mapper.selectSummary(hotelName);
	}

}
